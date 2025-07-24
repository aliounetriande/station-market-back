package com.stationmarket.api.vendor.controller;

import com.stationmarket.api.auth.security.CustomUserDetails;
import com.stationmarket.api.vendor.dto.MarketplaceDto;
import com.stationmarket.api.vendor.model.Marketplace;
import com.stationmarket.api.vendor.model.Vendor;
import com.stationmarket.api.vendor.service.MarketplaceService;
import com.stationmarket.api.vendor.exception.MarketplaceNotFoundException;
import com.stationmarket.api.vendor.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stationmarket/vendor/marketplaces")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class MarketplaceController {

    private final MarketplaceService marketplaceService;
    private final VendorService vendorService;

    // Vérification du rôle via @PreAuthorize (ROLE_VENDOR)
    @PreAuthorize("hasAuthority('ROLE_VENDOR')")
    @PostMapping("/create")
    @Transactional
    public ResponseEntity<Map<String, Object>> createMarketplace(
            @RequestBody MarketplaceDto marketplaceDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 1) on récupère le Vendor lié à l'utilisateur
        Vendor vendor = vendorService.findByUserId(userDetails.getId())
                .orElseThrow(
                        () -> new SecurityException("Le vendeur doit être connecté.")
                );

        //2) Création de la marketplace et association avec le vendeur
        Marketplace createdMarketplace = marketplaceService.createMarketplace(marketplaceDto, vendor);

        // 3) réponse 201 avec l'entité créée
        Map<String, Object> response = new HashMap<>();
        response.put("id", createdMarketplace.getId());
        response.put("marketName", createdMarketplace.getMarketName());
        response.put("shortDes", createdMarketplace.getShortDes());
        response.put("description", createdMarketplace.getDescription());
        response.put("email", createdMarketplace.getEmail());
        response.put("phone", createdMarketplace.getPhone());
        response.put("themeColor", createdMarketplace.getThemeColor());
        response.put("maintenanceMode", createdMarketplace.getMaintenanceMode());
        //response.put("createdAt", createdMarketplace.getCreatedAt());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Mise à jour de la marketplace
    @PreAuthorize("hasAuthority('ROLE_VENDOR')")
    @PutMapping("/update/{id}")
    @Transactional
    public ResponseEntity<Marketplace> updateMarketplace(
            @PathVariable Long id,
            @RequestBody MarketplaceDto marketplaceDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Vendor vendor = vendorService.findByUserId(userDetails.getId())
                .orElseThrow(() -> new SecurityException("Le vendeur doit être connecté."));

        Marketplace updatedMarketplace = marketplaceService.updateMarketplace(id, marketplaceDto, vendor);
        return ResponseEntity.ok(updatedMarketplace);
    }

    @PreAuthorize("hasAuthority('ROLE_VENDOR')")
    @GetMapping("/all")
    @Transactional
    public ResponseEntity<List<Marketplace>> listMyMarketplaces(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // 1. Récupère le Vendor à partir de l'utilisateur authentifié
        Vendor vendor = vendorService.findByUserId(userDetails.getId())
                .orElseThrow(() -> new SecurityException("Le vendeur doit être connecté."));

        // 2. Récupère toutes ses marketplaces
        List<Marketplace> list = marketplaceService.getMarketplacesByVendor(vendor);

        // 3. Renvoie 204 si aucune
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

/**
    // Récupérer la marketplace d'un vendeur
    @GetMapping("/vendor/{id}")
    @PreAuthorize("hasAuthority('ROLE_VENDOR')")
    public ResponseEntity<Marketplace> getMarketplaceByVendor(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Vendor vendor = vendorService.findByUserId(userDetails.getId())
                .orElseThrow(() -> new SecurityException("Le vendeur doit être connecté."));

        return marketplaceService.getMarketplaceByVendor(vendor)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new MarketplaceNotFoundException("Aucune marketplace trouvée pour ce vendeur"));
    }
 */

    /**
     * GET /stationmarket/vendor/marketplaces/{id}
     * Récupère une marketplace par son ID
     */
    @PreAuthorize("hasAuthority('ROLE_VENDOR')")
    @GetMapping("/{id}")
    public ResponseEntity<Marketplace> getMarketplaceById(@PathVariable Long id,
                                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        // tu peux aussi vérifier ici que userDetails a bien accès à cette marketplace
        return marketplaceService.getMarketplaceById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new MarketplaceNotFoundException("Marketplace non trouvée"));
    }


    // Gestion des erreurs pour MarketplaceNotFoundException
    @ExceptionHandler(MarketplaceNotFoundException.class)
    public ResponseEntity<String> handleMarketplaceNotFound(MarketplaceNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // Gestion des erreurs pour SecurityException (problème de permissions)
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> handleSecurityException(SecurityException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    // Gestion des erreurs génériques
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return new ResponseEntity<>("Erreur interne du serveur : " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
