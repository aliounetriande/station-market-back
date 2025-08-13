package com.stationmarket.api.vendor.service;

import com.stationmarket.api.vendor.model.Marketplace;
import com.stationmarket.api.vendor.model.MarketplaceEditor;
import com.stationmarket.api.vendor.repository.MarketplaceRepository;
import com.stationmarket.api.auth.model.User;
import com.stationmarket.api.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarketplaceEditorService {

    private final MarketplaceRepository marketplaceRepository;
    private final UserService userService;
    private final EntityManager entityManager;

    /**
     * Vérifie si un utilisateur a accès à une marketplace (OWNER ou EDITOR invité)
     */
    public boolean hasUserAccess(String userEmail, String marketplaceSlug) {
        log.info("🔍 [MARKETPLACE EDITOR] Vérification accès {} sur marketplace {}", userEmail, marketplaceSlug);

        try {
            // 1. Récupérer l'utilisateur
            User user = userService.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + userEmail));

            // 2. Récupérer la marketplace
            Marketplace marketplace = marketplaceRepository.findBySlug(marketplaceSlug)
                    .orElseThrow(() -> new RuntimeException("Marketplace non trouvée: " + marketplaceSlug));

            // 3. Vérifier si l'utilisateur est le propriétaire (OWNER)
            if (marketplace.getVendor() != null && marketplace.getVendor().getUser() != null) {
                Long ownerId = marketplace.getVendor().getUser().getId();
                if (ownerId.equals(user.getId())) {
                    log.info("✅ [MARKETPLACE EDITOR] OWNER détecté pour {}", userEmail);
                    return true;
                }
            }

            // 4. Vérifier si l'utilisateur est un EDITOR invité
            String jpql = """
                SELECT me FROM MarketplaceEditor me 
                WHERE me.user.id = :userId 
                AND me.marketplace.id = :marketplaceId
                """;

            TypedQuery<MarketplaceEditor> query = entityManager.createQuery(jpql, MarketplaceEditor.class);
            query.setParameter("userId", user.getId());
            query.setParameter("marketplaceId", marketplace.getId());

            List<MarketplaceEditor> editors = query.getResultList();

            if (!editors.isEmpty()) {
                MarketplaceEditor editor = editors.get(0);
                log.info("✅ [MARKETPLACE EDITOR] EDITOR détecté pour {} avec rôle: {}", userEmail, editor.getRole());
                return true;
            }

            log.info("❌ [MARKETPLACE EDITOR] Aucun accès pour {} sur {}", userEmail, marketplaceSlug);
            return false;

        } catch (Exception e) {
            log.error("❌ [MARKETPLACE EDITOR] Erreur vérification accès: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Version avec IDs pour compatibilité
     */
    public boolean hasUserAccess(Long userId, Long marketplaceId) {
        log.info("🔍 [MARKETPLACE EDITOR] Vérification accès userId={} marketplaceId={}", userId, marketplaceId);

        try {
            // 1. Récupérer la marketplace
            Marketplace marketplace = marketplaceRepository.findById(marketplaceId)
                    .orElseThrow(() -> new RuntimeException("Marketplace non trouvée: " + marketplaceId));

            // 2. Vérifier si l'utilisateur est le propriétaire (OWNER)
            if (marketplace.getVendor() != null && marketplace.getVendor().getUser() != null) {
                Long ownerId = marketplace.getVendor().getUser().getId();
                if (ownerId.equals(userId)) {
                    log.info("✅ [MARKETPLACE EDITOR] OWNER détecté pour userId={}", userId);
                    return true;
                }
            }

            // 3. Vérifier si l'utilisateur est un EDITOR invité
            String jpql = """
                SELECT me FROM MarketplaceEditor me 
                WHERE me.user.id = :userId 
                AND me.marketplace.id = :marketplaceId
                """;

            TypedQuery<MarketplaceEditor> query = entityManager.createQuery(jpql, MarketplaceEditor.class);
            query.setParameter("userId", userId);
            query.setParameter("marketplaceId", marketplaceId);

            List<MarketplaceEditor> editors = query.getResultList();

            if (!editors.isEmpty()) {
                log.info("✅ [MARKETPLACE EDITOR] EDITOR détecté pour userId={}", userId);
                return true;
            }

            log.info("❌ [MARKETPLACE EDITOR] Aucun accès pour userId={} marketplaceId={}", userId, marketplaceId);
            return false;

        } catch (Exception e) {
            log.error("❌ [MARKETPLACE EDITOR] Erreur vérification accès: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Récupère le rôle d'un utilisateur sur une marketplace
     */
    public String getUserRole(String userEmail, String marketplaceSlug) {
        log.info("🔍 [MARKETPLACE EDITOR] Récupération rôle {} sur {}", userEmail, marketplaceSlug);

        try {
            User user = userService.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + userEmail));

            Marketplace marketplace = marketplaceRepository.findBySlug(marketplaceSlug)
                    .orElseThrow(() -> new RuntimeException("Marketplace non trouvée: " + marketplaceSlug));

            // Vérifier si c'est le propriétaire
            if (marketplace.getVendor() != null && marketplace.getVendor().getUser() != null) {
                Long ownerId = marketplace.getVendor().getUser().getId();
                if (ownerId.equals(user.getId())) {
                    return "OWNER";
                }
            }

            // Vérifier si c'est un éditeur
            String jpql = """
                SELECT me FROM MarketplaceEditor me 
                WHERE me.user.id = :userId 
                AND me.marketplace.id = :marketplaceId
                """;

            TypedQuery<MarketplaceEditor> query = entityManager.createQuery(jpql, MarketplaceEditor.class);
            query.setParameter("userId", user.getId());
            query.setParameter("marketplaceId", marketplace.getId());

            List<MarketplaceEditor> editors = query.getResultList();

            if (!editors.isEmpty()) {
                return editors.get(0).getRole(); // Retourne "EDITOR" ou autre rôle
            }

            return null; // Aucun rôle

        } catch (Exception e) {
            log.error("❌ [MARKETPLACE EDITOR] Erreur récupération rôle: {}", e.getMessage(), e);
            return null;
        }
    }
}