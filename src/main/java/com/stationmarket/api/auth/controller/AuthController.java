package com.stationmarket.api.auth.controller;

import com.stationmarket.api.auth.model.User;
import com.stationmarket.api.common.dto.*;
import com.stationmarket.api.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/stationmarket/auth")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

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
}
