package com.stationmarket.api.payments.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stationmarket.api.payments.config.LigdicashProperties;
import com.stationmarket.api.payments.dto.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class LigdicashService {

    private final RestTemplate restTemplate;
    private final LigdicashProperties ligdicashProperties;
    private final ObjectMapper objectMapper; // ✅ Injection d'ObjectMapper

    /**
     * Créer une facture Ligdicash
     */
    public LigdicashInvoiceResponse createInvoice(LigdicashInvoiceRequest request) {
        log.info("🏗️ [LIGDICASH] Création facture - Total: {} FCFA, Items: {}",
                request.getInvoice().getTotalAmount(),
                request.getInvoice().getItems().size());

        try {
            // ✅ SÉRIALISATION MANUELLE avec ObjectMapper injecté
            String jsonRequest = objectMapper.writeValueAsString(request);
            log.info("🔍 [DEBUG] JSON exact envoyé à Ligdicash:");
            log.info("🔍 [DEBUG] {}", jsonRequest);

            String url = ligdicashProperties.getApi().getBaseUrl() + "/checkout-invoice/create";
            HttpHeaders headers = createHeaders();

            // ✅ CORRECTION CRITIQUE : Envoyer le JSON String directement
            HttpEntity<String> entity = new HttpEntity<>(jsonRequest, headers);

            log.info("📤 [LIGDICASH] URL: {}", url);

            ResponseEntity<LigdicashInvoiceResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    LigdicashInvoiceResponse.class
            );

            LigdicashInvoiceResponse result = response.getBody();

            // ✅ DEBUG : Voir la réponse complète
            log.info("🔍 [DEBUG] Réponse complète Ligdicash: {}", result);

            String tokenPreview = "NULL";
            if (result != null && result.getToken() != null && !result.getToken().isEmpty()) {
                int previewLength = Math.min(20, result.getToken().length());
                tokenPreview = result.getToken().substring(0, previewLength) + "...";
            }

            log.info("✅ [LIGDICASH] Facture créée - Code: {}, Token: {}",
                    result != null ? result.getResponseCode() : "NULL",
                    tokenPreview);

            return result;

        } catch (Exception e) {
            log.error("❌ [LIGDICASH] Erreur création facture", e);
            return LigdicashInvoiceResponse.builder()
                    .responseCode("01")
                    .responseText("Erreur de communication avec Ligdicash: " + e.getMessage())
                    .description("Erreur technique")
                    .build();
        }
    }

    /**
     * Confirmer le statut d'un paiement
     */
    public LigdicashConfirmResponse confirmPayment(String token) {
        log.info("🔍 [LIGDICASH] Vérification paiement - Token: {}",
                token.substring(0, Math.min(token.length(), 20)) + "...");

        try {
            String url = ligdicashProperties.getApi().getBaseUrl() +
                    "/checkout-invoice/confirm/?invoiceToken=" + token;

            HttpHeaders headers = createHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            log.info("📤 [LIGDICASH] URL confirm: {}", url);

            ResponseEntity<LigdicashConfirmResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    LigdicashConfirmResponse.class
            );

            LigdicashConfirmResponse result = response.getBody();

            log.info("🔍 [DEBUG] Réponse confirm complète: {}", result);
            log.info("✅ [LIGDICASH] Statut vérifié - Code: {}, Status: {}, Amount: {}",
                    result != null ? result.getResponseCode() : "NULL",
                    result != null ? result.getStatus() : "NULL",
                    result != null ? result.getAmount() : "NULL");

            return result;

        } catch (RestClientException e) {
            log.error("❌ [LIGDICASH] Erreur vérification paiement", e);
            return LigdicashConfirmResponse.builder()
                    .responseCode("01")
                    .responseText("Erreur de communication avec Ligdicash: " + e.getMessage())
                    .description("Erreur technique")
                    .status("error")
                    .build();
        }
    }

    /**
     * Créer les headers HTTP pour Ligdicash
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "application/json");

        String apiKey = ligdicashProperties.getApi().getApiKey();
        String apiToken = ligdicashProperties.getApi().getApiToken();

        // ✅ VALIDATION ET NETTOYAGE
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.error("❌ [LIGDICASH] API Key manquante !");
            throw new IllegalStateException("API Key Ligdicash manquante");
        }

        if (apiToken == null || apiToken.trim().isEmpty()) {
            log.error("❌ [LIGDICASH] API Token manquant !");
            throw new IllegalStateException("API Token Ligdicash manquant");
        }

        String cleanApiKey = apiKey.trim();
        String cleanApiToken = apiToken.trim();

        headers.set("Apikey", cleanApiKey);
        headers.set("Authorization", "Bearer " + cleanApiToken);

        log.debug("🔑 [LIGDICASH] Headers configurés - ApiKey: {}..., Token: {}...",
                cleanApiKey.substring(0, Math.min(5, cleanApiKey.length())),
                cleanApiToken.substring(0, Math.min(5, cleanApiToken.length())));

        return headers;
    }

    /**
     * Debug des propriétés au démarrage
     */
    @PostConstruct
    public void debugProperties() {
        log.info("🔧 [LIGDICASH CONFIG] Propriétés chargées:");
        log.info("🔧 [LIGDICASH CONFIG] Base URL: {}", ligdicashProperties.getApi().getBaseUrl());
        log.info("🔧 [LIGDICASH CONFIG] Store Name: {}", ligdicashProperties.getStore().getName());

        String apiKey = ligdicashProperties.getApi().getApiKey();
        String apiToken = ligdicashProperties.getApi().getApiToken();

        if (apiKey != null && !apiKey.isEmpty()) {
            log.info("🔧 [LIGDICASH CONFIG] API Key: {}... (length: {})",
                    apiKey.substring(0, Math.min(5, apiKey.length())),
                    apiKey.length());
        } else {
            log.warn("⚠️ [LIGDICASH CONFIG] API Key manquante !");
        }

        if (apiToken != null && !apiToken.isEmpty()) {
            log.info("🔧 [LIGDICASH CONFIG] API Token: {}... (length: {})",
                    apiToken.substring(0, Math.min(10, apiToken.length())),
                    apiToken.length());
        } else {
            log.warn("⚠️ [LIGDICASH CONFIG] API Token manquant !");
        }
    }
}