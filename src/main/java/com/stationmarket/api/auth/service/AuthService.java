package com.stationmarket.api.auth.service;

import com.stationmarket.api.auth.model.Role;
import com.stationmarket.api.auth.model.Status;
import com.stationmarket.api.auth.model.User;
import com.stationmarket.api.auth.model.VerificationToken;
import com.stationmarket.api.auth.repository.RoleRepository;
import com.stationmarket.api.auth.repository.TokenRepository;
import com.stationmarket.api.auth.repository.UserRepository;
import com.stationmarket.api.common.dto.LoginRequest;
import com.stationmarket.api.common.dto.ProfileUpdateDto;
import com.stationmarket.api.common.dto.SignupRequest;
import com.stationmarket.api.auth.security.CustomUserDetails;
import com.stationmarket.api.auth.security.JwtUtils;
import com.stationmarket.api.common.dto.JwtResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final TokenRepository tokenRepo;
    private final PasswordEncoder encoder;
    private final JavaMailSender mailSender;

    @Value("${app.front-url}")
    private String frontUrl;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private long jwtExpirationMs;

    public JwtResponse authenticate(LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(userDetails);
        List<String> roles = userDetails.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .toList();
        return JwtResponse.builder()
                .token(jwt)
                .type("Bearer")
                .id(userDetails.getId())
                .name(userDetails.getUsername())
                .email(userDetails.getUsername())
                .roles(roles)
                .build();
    }

    @Transactional
    public String register(SignupRequest req) {
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }
        User u = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(encoder.encode(req.getPassword()))
                .status(Status.INACTIVE)
                .build();

        Role vendorRole = roleRepo.findByName(Role.RoleName.ROLE_VENDOR)
                .orElseThrow(() -> new RuntimeException("Role VENDOR introuvable"));
        Set<Role> roles = new HashSet<>();
        roles.add(vendorRole);
        u.setRoles(roles);

        userRepo.save(u);

        String token = UUID.randomUUID().toString();
        VerificationToken vToken = VerificationToken.builder()
                .token(token)
                .user(u)
                .expiryDate(LocalDateTime.now().plusHours(24))
                .build();
        tokenRepo.save(vToken);

        String link = frontUrl + "/stationmarket/auth/confirm?token=" + token;
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(u.getEmail());
        mail.setSubject("Activez votre compte Station Market");
        mail.setText(
                "Bonjour " + u.getName() + ",\n\n" +
                        "Merci de cliquer sur ce lien pour activer votre compte STATION MARKET :\n" + link +
                        "\n\nCe lien expire dans 24h."
        );
        mailSender.send(mail);

        return "Inscription réussie, vérifiez votre e-mail pour activer votre compte.";
    }

    @Transactional
    public String confirmToken(String token) {
        VerificationToken v = tokenRepo.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token invalide"));
        if (v.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expiré");
        }
        User u = v.getUser();
        u.setStatus(Status.ACTIVE);
        userRepo.save(u);
        tokenRepo.deleteByToken(token);
        return "Votre compte Vendor est activé ! Vous pouvez vous connecter.";
    }

    public User getCurrentUser(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
    }

    @Transactional
    public User updateProfile(String email, ProfileUpdateDto dto) {
        User u = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
        u.setName(dto.getName());
        u.setEmail(dto.getEmail());
        // si tu veux gérer mot de passe/photo, etc. : u.setPassword(…), u.setPhoto(…)
        return userRepo.save(u);
    }

    public String generateJwtToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .claim("invitedAs", user.getInvitedAs()) // Si applicable
                .claim("profileCompleted", user.getProfileCompleted()) // Si applicable
                .claim("roles", user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toList()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
}
