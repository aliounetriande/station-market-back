package com.stationmarket.api.vendor.controller;

import com.stationmarket.api.auth.security.CustomUserDetails;
import com.stationmarket.api.vendor.dto.InvitationDto;
import com.stationmarket.api.vendor.dto.InvitationRequestDto;
import com.stationmarket.api.vendor.dto.AcceptInvitationRequestDto;
import com.stationmarket.api.vendor.model.Vendor;
import com.stationmarket.api.vendor.model.VendorCategory;
import com.stationmarket.api.vendor.repository.VendorRepository;
import com.stationmarket.api.vendor.service.InvitationService;
import com.stationmarket.api.auth.service.UserService;
import com.stationmarket.api.auth.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invitations")
@CrossOrigin(origins = {"http://localhost:4200", "https://station-market.total-innovation.net"}, allowedHeaders = "*")
public class InvitationController {

    private final InvitationService invitationService;
    private final UserService userService;
    private final VendorRepository vendorRepository;

    @Autowired
    public InvitationController(InvitationService invitationService, UserService userService, VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
        this.userService = userService;
        this.invitationService = invitationService;
    }

    @PostMapping("/marketplace/{marketplaceSlug}/invite")
    public ResponseEntity<String> sendInvitation(
            @PathVariable String marketplaceSlug,
            @RequestBody InvitationRequestDto request,
            Authentication authentication) {

        // Récupérer l'utilisateur connecté
        User currentUser = null;
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            currentUser = (User) authentication.getPrincipal();
        } else if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            // Récupérer l'utilisateur depuis le service
            currentUser = userService.findByEmail(userDetails.getUsername())
                    .orElse(null); // Utiliser orElse(null) pour gérer le cas où l'utilisateur n'est pas trouvé;
        }

        // Vérifier que currentUser n'est pas null
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non authentifié");
        }

        // Forcer le rôle à VENDOR, quelle que soit la valeur fournie
        request.setRole("ROLE_VENDOR");

        String result = invitationService.sendInvitation(marketplaceSlug, request, currentUser);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/marketplace/{marketplaceSlug}/pending")
    public ResponseEntity<List<InvitationDto>> getPendingInvitations(
            @PathVariable String marketplaceSlug) {

        List<InvitationDto> invitations = invitationService.getPendingInvitations(marketplaceSlug);
        return ResponseEntity.ok(invitations);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelInvitation(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {

        invitationService.cancelInvitation(id, currentUser);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/accept-invitation")
    public ResponseEntity<Map<String, String>> acceptInvitation(@RequestBody AcceptInvitationRequestDto request) {
        try {
            User user = invitationService.acceptInvitation(request);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Invitation acceptée et Vendor créé avec succès.");
            response.put("email", user.getEmail());
            response.put("status", "SUCCESS");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'acceptation: " + e.getMessage());

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("status", "ERROR");

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }


//    @PostMapping("/accept")
//    public ResponseEntity<String> acceptInvitation(@RequestBody AcceptInvitationRequestDto request) {
//        // Vérifier uniquement si le token est valide
//        String result = invitationService.validateInvitationToken(request.getToken());
//        return ResponseEntity.ok(result);
//    }

//    @PostMapping("/accept-complete")
//    public ResponseEntity<String> acceptInvitationComplete(@RequestBody AcceptInvitationRequestDto request) {
//        String result = invitationService.acceptInvitation(request);
//        return ResponseEntity.ok(result);
//    }

    @GetMapping("/validate/{token}")
    public ResponseEntity<InvitationDto> getInvitationDetails(@PathVariable String token) {
        InvitationDto invitation = invitationService.getInvitationByToken(token);
        return ResponseEntity.ok(invitation);
    }




}