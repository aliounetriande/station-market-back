package com.stationmarket.api.payments.controller;

import com.stationmarket.api.order.model.Order;
import com.stationmarket.api.payments.config.LigdicashProperties;
import com.stationmarket.api.payments.dto.*;
import com.stationmarket.api.payments.model.PaymentIntent;
import com.stationmarket.api.payments.repository.PaymentIntentRepository;
import com.stationmarket.api.payments.service.LigdicashService;
import com.stationmarket.api.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class PaymentController {

    private final LigdicashService ligdicashService;
    private final OrderService orderService;
    private final LigdicashProperties ligdicashProperties;

    /**
     * Test de la configuration Ligdicash
     */
    @GetMapping("/test-config")
    public Map<String, Object> testConfig() {
        log.info("🧪 [TEST] Vérification configuration Ligdicash");

        return Map.of(
                "status", "ok",
                "config", Map.of(
                        "baseUrl", ligdicashProperties.getApi().getBaseUrl(),
                        "apiKeyConfigured", ligdicashProperties.getApi().getApiKey() != null &&
                                !ligdicashProperties.getApi().getApiKey().equals("your-api-key-here"),
                        "apiTokenConfigured", ligdicashProperties.getApi().getApiToken() != null &&
                                !ligdicashProperties.getApi().getApiToken().equals("your-api-token-here"),
                        "storeName", ligdicashProperties.getStore().getName(),
                        "websiteUrl", ligdicashProperties.getStore().getWebsiteUrl(),
                        "successUrl", ligdicashProperties.getUrls().getSuccess(),
                        "cancelUrl", ligdicashProperties.getUrls().getCancel(),
                        "callbackUrl", ligdicashProperties.getUrls().getCallback()
                )
        );
    }

    /**
     * Créer une facture Ligdicash
     */

    @Autowired
    private PaymentIntentRepository paymentIntentRepository;

    @PostMapping("/ligdicash/create-invoice")
    public ResponseEntity<LigdicashInvoiceResponse> createInvoice(@RequestBody LigdicashInvoiceRequest request) {
        log.info("CustomData reçu: {}", request.getCustomData());
        // ✅ CORRECTION : Accès direct aux données
        log.info("💳 [PAYMENT API] Création facture - Total: {} FCFA",
                request.getInvoice().getTotalAmount());

        try {
            // Récupère les infos utiles
            String orderId = (String) request.getCustomData().get("order_id");
            String marketplaceSlug = (String) request.getCustomData().get("marketplace_slug");
            log.info("marketplaceSlug reçu dans createInvoice: {}", marketplaceSlug);
            String userEmail = (String) request.getCustomData().get("user_email");
            Integer amount = request.getInvoice().getTotalAmount();

            // Sauvegarde le mapping temporaire
            PaymentIntent intent = PaymentIntent.builder()
                    .orderId(orderId)
                    .marketplaceSlug(marketplaceSlug)
                    .userEmail(userEmail)
                    .amount(amount)
                    .status("PENDING")
                    .deliveryLat((Double) request.getCustomData().get("deliveryLat"))
                    .deliveryLng((Double) request.getCustomData().get("deliveryLng"))
                    .deliveryAddress((String) request.getCustomData().get("deliveryAddress"))
                    .deliveryMode((String) request.getCustomData().get("deliveryMode"))
                    .firstName((String) request.getCustomData().get("firstName"))
                    .lastName((String) request.getCustomData().get("lastName"))
                    .build();
            paymentIntentRepository.save(intent);

            LigdicashInvoiceResponse response = ligdicashService.createInvoice(request);

            log.info("✅ [PAYMENT API] Facture traitée - Code: {}", response.getResponseCode());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ [PAYMENT API] Erreur création facture", e);

            LigdicashInvoiceResponse errorResponse = LigdicashInvoiceResponse.builder()
                    .responseCode("01")
                    .responseText("Erreur serveur: " + e.getMessage())
                    .description("Erreur interne")
                    .build();

            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * Confirmer le statut d'un paiement
     */
    // ...existing code...
    @GetMapping("/ligdicash/confirm/{token}")
    public ResponseEntity<LigdicashConfirmResponse> confirmPayment(@PathVariable String token) {
        log.info("🔍 [PAYMENT API] Vérification paiement - Token: {}",
                token.substring(0, Math.min(token.length(), 20)) + "...");

        try {
            LigdicashConfirmResponse response = ligdicashService.confirmPayment(token);

            log.info("✅ [PAYMENT API] Statut vérifié - Code: {}, Status: {}",
                    response.getResponseCode(), response.getStatus());

            // Ajout : Création de la commande si paiement valide

            if ("completed".equalsIgnoreCase(response.getStatus())) {
                String orderId = null;
                String transactionIdFromLigdicash = null;

                if (response.getCustomData() != null) {
                    for (var cd : response.getCustomData()) {
                        if ("order_id".equals(cd.getKeyOfCustomData())) {
                            orderId = cd.getValueOfCustomData();
                        }
                        if ("transaction_id".equals(cd.getKeyOfCustomData())) { // <-- Récupère le transaction_id
                            transactionIdFromLigdicash = cd.getValueOfCustomData();
                        }
                    }
                }

                if (orderId != null) {
                    PaymentIntent intent = paymentIntentRepository.findById(orderId).orElse(null);
                    if (intent != null) {
                        // Création de la commande
                        Order order = new Order();
                        order.setMarketplaceSlug(intent.getMarketplaceSlug());
                        order.setAmount(new BigDecimal(intent.getAmount()));
                        order.setStatus("PAID");
                        order.setUserEmail(intent.getUserEmail());
                        order.setDeliveryLat(intent.getDeliveryLat());
                        order.setDeliveryLng(intent.getDeliveryLng());
                        order.setDeliveryAddress(intent.getDeliveryAddress());
                        order.setDeliveryMode(intent.getDeliveryMode());
                        order.setTransactionId(transactionIdFromLigdicash);
                        order.setCreatedAt(java.time.LocalDateTime.now());
                        orderService.save(order);

                        // Mets à jour le statut du PaymentIntent si besoin
                        intent.setStatus("PAID");
                        paymentIntentRepository.save(intent);
                    } else {
                        log.warn("❌ Impossible de retrouver le mapping PaymentIntent pour orderId={}", orderId);
                    }
                }
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ [PAYMENT API] Erreur vérification paiement", e);

            LigdicashConfirmResponse errorResponse = LigdicashConfirmResponse.builder()
                    .responseCode("01")
                    .responseText("Erreur serveur: " + e.getMessage())
                    .description("Erreur interne")
                    .status("error")
                    .build();

            return ResponseEntity.status(500).body(errorResponse);
        }

    }
// ...existing code...
    /**
     * Callback de Ligdicash (webhook)
     */
    @PostMapping("/ligdicash/callback")
    public ResponseEntity<Map<String, String>> handleCallback(
            @RequestBody Map<String, Object> payload,
            HttpServletRequest request) {

        log.info("📞 [PAYMENT CALLBACK] Callback reçu depuis Ligdicash");
        log.info("📞 [PAYMENT CALLBACK] IP: {}", request.getRemoteAddr());
        log.info("📞 [PAYMENT CALLBACK] Payload: {}", payload);

        try {
            String status = (String) payload.get("status");
            String transactionId = (String) payload.get("transaction_id");
            String orderId = (String) payload.get("order_id");

            log.info("📞 [PAYMENT CALLBACK] Transaction: {} - Status: {}", transactionId, status);

            if ("completed".equals(status)) {
                log.info("✅ [PAYMENT CALLBACK] Paiement confirmé par callback");

                // Exemple de récupération des infos (à adapter selon ton payload)
                String marketplaceSlug = (String) payload.get("marketplace_slug");
                BigDecimal amount = new BigDecimal(payload.get("amount").toString());
                String userEmail = (String) payload.get("user_email");

                // Récupère le PaymentIntent pour avoir la localisation
                PaymentIntent intent = null;
                if (orderId != null) {
                    intent = paymentIntentRepository.findById(orderId).orElse(null);
                }

                Order order = new Order();
                if (intent != null) {
                    order.setMarketplaceSlug(intent.getMarketplaceSlug());
                    order.setAmount(new BigDecimal(intent.getAmount()));
                    order.setStatus("PAID");
                    order.setUserEmail(intent.getUserEmail());
                    order.setDeliveryLat(intent.getDeliveryLat());
                    order.setDeliveryLng(intent.getDeliveryLng());
                    order.setDeliveryAddress(intent.getDeliveryAddress());
                    order.setDeliveryMode(intent.getDeliveryMode());
                } else {
                    // fallback si pas de PaymentIntent
                    order.setMarketplaceSlug((String) payload.get("marketplace_slug"));
                    order.setAmount(new BigDecimal(payload.get("amount").toString()));
                    order.setStatus("PAID");
                    order.setUserEmail((String) payload.get("user_email"));
                }
                order.setTransactionId(transactionId);
                order.setCreatedAt(java.time.LocalDateTime.now());

                orderService.save(order); // Ajoute cette méthode dans OrderService si besoin
            } else {
                log.warn("⚠️ [PAYMENT CALLBACK] Paiement non confirmé: {}", status);
            }

            return ResponseEntity.ok(Map.of(
                    "status", "received",
                    "message", "Callback traité avec succès"
            ));

        } catch (Exception e) {
            log.error("❌ [PAYMENT CALLBACK] Erreur traitement callback", e);
            return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "message", "Erreur traitement callback"
            ));
        }
    }
}
