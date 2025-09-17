package com.stationmarket.api.delivery.controller;

import com.stationmarket.api.delivery.dto.CreateDeliveryAgentDto;
import com.stationmarket.api.delivery.dto.DeliveryAgentProfileDto;
import com.stationmarket.api.delivery.dto.UpdateDeliveryAgentDto;
import com.stationmarket.api.delivery.model.DeliveryAgent;
import com.stationmarket.api.delivery.service.DeliveryAgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/delivery-agents")
@RequiredArgsConstructor
public class DeliveryAgentController {
    private final DeliveryAgentService deliveryAgentService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_VENDOR')")
    public ResponseEntity<DeliveryAgent> createDeliveryAgent(@RequestBody CreateDeliveryAgentDto dto) {
        DeliveryAgent agent = deliveryAgentService.createDeliveryAgent(
                dto.getName(),
                dto.getEmail(),
                dto.getPhone(),
                dto.getAddress(),
                dto.getPassword(),
                dto.getMarketplaceId()
        );
        return ResponseEntity.ok(agent);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_VENDOR')")
    public ResponseEntity<DeliveryAgent> updateDeliveryAgent(
            @PathVariable Long id,
            @RequestBody UpdateDeliveryAgentDto dto) {
        DeliveryAgent agent = deliveryAgentService.updateDeliveryAgent(id, dto);
        return ResponseEntity.ok(agent);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_VENDOR')")
    public ResponseEntity<DeliveryAgent> getById(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryAgentService.getById(id));
    }

    @GetMapping(produces = "application/json")
    @PreAuthorize("hasRole('ROLE_VENDOR')")
    public ResponseEntity<List<DeliveryAgent>> getByMarketplace(@RequestParam Long marketplaceId) {
        return ResponseEntity.ok(deliveryAgentService.getByMarketplace(marketplaceId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_VENDOR')")
    public ResponseEntity<Void> deleteDeliveryAgent(@PathVariable Long id) {
        deliveryAgentService.deleteDeliveryAgent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ROLE_DELIVERY')")
    public ResponseEntity<DeliveryAgentProfileDto> getMyProfile(java.security.Principal principal) {
                if (principal == null || principal.getName() == null) {
                        return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
                    }
                String email = principal.getName();
                DeliveryAgentProfileDto profile = deliveryAgentService.getProfileByEmail(email);
                return ResponseEntity.ok(profile);
            }
}
