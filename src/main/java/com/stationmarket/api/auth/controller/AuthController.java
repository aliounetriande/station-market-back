package com.stationmarket.api.auth.controller;

import com.stationmarket.api.auth.model.User;
import com.stationmarket.api.auth.repository.UserRepository;
import com.stationmarket.api.common.dto.*;
import com.stationmarket.api.auth.service.AuthService;
import com.stationmarket.api.vendor.model.Vendor;
import com.stationmarket.api.vendor.model.VendorCategory;
import com.stationmarket.api.vendor.repository.VendorRepository;
import com.stationmarket.api.vendor.service.PackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/stationmarket/auth")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final PackService packService;
    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;


    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.authenticate(req));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody SignupRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    /**
     * Appelé quand l’utilisateur clique sur le lien reçu par e-mail.
     */
    @GetMapping("/confirm")
    public ResponseEntity<String> confirm(@RequestParam("token") String token) {
        try {
            String message = authService.confirmToken(token);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            // token inexistant ou déjà consommé
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Jeton d'activation invalide ou déjà utilisé."
            );
        } catch (Exception e) {
            // pour couvrir le cas 'token expiré' ou autres
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    e.getMessage()
            );
        }
    }

    /** Récupérer le profil de l’utilisateur connecté */
//    @GetMapping("/profile")
//    public ResponseEntity<User> profile(Authentication auth) {
//        String email = auth.getName();
//        User u = authService.getCurrentUser(email);
//        return ResponseEntity.ok(u);
//    }

    /** Mettre à jour le profil */
    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(
            Authentication auth,
            @Valid @RequestBody ProfileUpdateDto dto
    ) {
        String email = auth.getName();
        User updated = authService.updateProfile(email, dto);
        return ResponseEntity.ok(updated);
    }


    @PatchMapping("/complete-invited-profile")
    @PreAuthorize("hasRole('ROLE_VENDOR')")
    public ResponseEntity<?> completeInvitedProfile(Authentication auth, @RequestBody Map<String, Object> profileData) {
        String email = auth.getName();
        User user = authService.getCurrentUser(email);

        // ✅ Mise à jour du profil utilisateur
        user.setName((String) profileData.get("fullName"));
        user.setPhone((String) profileData.get("phone"));
        user.setAddress((String) profileData.get("address"));
        user.setProfileCompleted(true); // ✅ Marquer le profil comme complété
        userRepository.save(user);

        // ✅ Vérifier si un Vendor existe déjà pour cet utilisateur
        Optional<Vendor> existingVendor = vendorRepository.findByUserId(user.getId());
        if (existingVendor.isEmpty()) {
            // ✅ Créer un Vendor si nécessaire
            Vendor vendor = Vendor.builder()
                    .user(user)
                    .phone((String) profileData.get("phone"))
                    .address((String) profileData.get("address"))
                    .category(VendorCategory.Alimentation) // Par défaut, ou basé sur l'invitation
                    .pack(packService.getDefaultPack()) // Pack par défaut
                    .build();
            vendorRepository.save(vendor);
            System.out.println("✅ Vendor créé : " + vendor.getId());
        }

        // ✅ Générer un nouveau JWT
        String newToken = authService.generateJwtToken(user);

        // ✅ Retourner les infos mises à jour avec le nouveau token
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Profil complété avec succès !");
        response.put("token", newToken); // ✅ Nouveau token
        response.put("user", Map.of(
                "name", user.getName(),
                "email", user.getEmail(),
                "phone", user.getPhone(),
                "profileCompleted", true
        ));

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/mark-profile-partial")
    @PreAuthorize("hasRole('ROLE_VENDOR')")
    public ResponseEntity<?> markProfileAsPartial(@RequestBody Map<String, Object> data) {
        // Pour l'instant, juste retourner un succès pour tester
        System.out.println("⏭️ Skip profil : " + data);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Profil marqué comme partiel");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    @PreAuthorize("hasRole('ROLE_VENDOR')")
    public ResponseEntity<?> refreshToken(Authentication auth) {
        String email = auth.getName();
        User user = authService.getCurrentUser(email);

        // Générer un nouveau JWT
        String newToken = authService.generateJwtToken(user);

        return ResponseEntity.ok(Map.of(
                "token", newToken,
                "type", "Bearer"
        ));
    }


}
