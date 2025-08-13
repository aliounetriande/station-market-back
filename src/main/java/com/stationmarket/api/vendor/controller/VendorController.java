package com.stationmarket.api.vendor.controller;

import com.stationmarket.api.vendor.dto.VendorDto;
import com.stationmarket.api.vendor.dto.VendorSetupDto;
import com.stationmarket.api.vendor.dto.VendorUpdateDto;
import com.stationmarket.api.vendor.model.Vendor;
import com.stationmarket.api.vendor.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
public class VendorController {
    private final VendorService vendorService;

    /**
     * Appelé après login et confirmation email, pour créer l’entité Vendor.
     */
    @PostMapping("/setup")
    public ResponseEntity<Void> setupVendor(@Valid @RequestBody VendorSetupDto dto,
                                            Authentication auth) {
        String userEmail = auth.getName();
        vendorService.createVendor(userEmail, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_VENDOR')")
    public ResponseEntity<VendorDto> getMyVendor(Authentication auth) {
        String email = auth.getName();
        return vendorService.findByUserEmail(email)
                .map(v -> VendorDto.builder()
                        .id(v.getId())
                        .phone(v.getPhone())
                        .address(v.getAddress())
                        .category(v.getCategory().name())
                        .packId(v.getPack() != null ? v.getPack().getId() : null) // ✅ Gestion du null
                        // ← on récupère l’utilisateur associé
                        .email(v.getUser().getEmail())
                        .name(v.getUser().getName())
                        .password(v.getUser().getPassword())
                        .build()
                )
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    public ResponseEntity<VendorDto> updateMyVendor(
            @Valid @RequestBody VendorUpdateDto dto,
            Authentication auth
    ) {
        String email = auth.getName();
        VendorDto updated = vendorService.updateVendor(email, dto);
        return ResponseEntity.ok(updated);
    }

}
