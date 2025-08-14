package com.stationmarket.api.payments.controller;

import com.stationmarket.api.payments.config.LigdicashProperties;
import com.stationmarket.api.payments.dto.*;
import com.stationmarket.api.payments.service.LigdicashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class PaymentController {

    private final LigdicashService ligdicashService;
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
    @PostMapping("/ligdicash/create-invoice")
    public ResponseEntity<LigdicashInvoiceResponse> createInvoice(@RequestBody LigdicashInvoiceRequest request) {
        // ✅ CORRECTION : Accès direct aux données
        log.info("💳 [PAYMENT API] Création facture - Total: {} FCFA",
                request.getInvoice().getTotalAmount());

        try {
            // ✅ DIRECT : Plus besoin de conversion, passer directement la requête
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
    @GetMapping("/ligdicash/confirm/{token}")
    public ResponseEntity<LigdicashConfirmResponse> confirmPayment(@PathVariable String token) {
        log.info("🔍 [PAYMENT API] Vérification paiement - Token: {}",
                token.substring(0, Math.min(token.length(), 20)) + "...");

        try {
            LigdicashConfirmResponse response = ligdicashService.confirmPayment(token);

            log.info("✅ [PAYMENT API] Statut vérifié - Code: {}, Status: {}",
                    response.getResponseCode(), response.getStatus());

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

            log.info("📞 [PAYMENT CALLBACK] Transaction: {} - Status: {}", transactionId, status);

            if ("completed".equals(status)) {
                log.info("✅ [PAYMENT CALLBACK] Paiement confirmé par callback");
                // TODO: Finaliser la commande
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
