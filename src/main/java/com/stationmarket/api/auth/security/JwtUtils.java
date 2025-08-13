package com.stationmarket.api.auth.security;

import com.stationmarket.api.auth.model.User;
import com.stationmarket.api.auth.repository.UserRepository;
import com.stationmarket.api.auth.service.UserService;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.Date;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;


@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private long jwtExpirationMs;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository; // Assurez-vous d'avoir un UserRepository pour accéder aux

    // Génération du token
    // Dans votre JwtUtils.java
    public String generateJwtToken(CustomUserDetails userDetails) {
        // ✅ RÉCUPÉRER L'UTILISATEUR COMPLET
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .claim("phone", user.getPhone())
                .claim("address", user.getAddress())
                .claim("invitedAs", user.getInvitedAs()) // ✅ CRITIQUE
                .claim("profileCompleted", user.getProfileCompleted()) // ✅ CRITIQUE
                .claim("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }


//    public String generateJwtToken(CustomUserDetails userDetails) {
//        return Jwts.builder()
//                .setSubject(userDetails.getUsername())
//                // Utilisation de getAuthority() pour récupérer les rôles sous forme de String
//                .claim("roles", userDetails.getAuthorities().stream()
//                        .map(GrantedAuthority::getAuthority) // Récupère le rôle sous forme de chaîne
//                        .collect(Collectors.toList()))
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
//                .signWith(SignatureAlgorithm.HS512, jwtSecret)
//                .compact();
//    }


    public List<GrantedAuthority> getRolesFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        // Récupère le champ 'roles' dans le JWT
        List<String> roles = claims.get("roles", List.class);

        // Transforme les rôles en GrantedAuthority
        return roles.stream()
                .map(SimpleGrantedAuthority::new) // Transformation en SimpleGrantedAuthority
                .collect(Collectors.toList());
    }


    // Récupérer le username (email) du token
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Décodeur Base64 pour le token
    public Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Validation
    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            // malformed, expired, unsupported, signature invalid...
        }
        return false;
    }


}
