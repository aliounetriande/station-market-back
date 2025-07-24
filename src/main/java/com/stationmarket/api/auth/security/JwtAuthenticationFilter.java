package com.stationmarket.api.auth.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // on ne filtre pas /stationmarket/auth/** et les docs swagger
        return path.startsWith("/stationmarket/auth/") || path.startsWith("/stationmarket/swagger-ui/") || path.equals("/error");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                    FilterChain chain) throws ServletException, java.io.IOException {
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7); // Extraire le token sans le préfixe "Bearer "
            if (jwtUtils.validateJwtToken(token)) {
                String email = jwtUtils.getEmailFromToken(token);

                // Chargez les rôles à partir du JWT
                List<GrantedAuthority> authorities = jwtUtils.getRolesFromToken(token);

                // Chargez l'utilisateur à partir du CustomUserDetailsService
                UserDetails uds = userDetailsService.loadUserByUsername(email);

                // Affichez les rôles dans les logs pour vérifier
                System.out.println("Roles dans UserDetails: " + uds.getAuthorities());

                // Combinez les autorités extraites du JWT avec celles du UserDetails (si nécessaire)
                List<GrantedAuthority> allAuthorities = authorities.stream()
                        .collect(Collectors.toList()); // Conservez uniquement les rôles extraits du JWT

                // Créez un token d'authentification avec les rôles combinés
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        uds, null, allAuthorities);  // Attribuez les rôles au token

                // Définissez l'authentification dans le contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authToken);

                System.out.println("Roles extraits du JWT: " + authorities);
            }
        }
        System.out.println("Roles dans le SecurityContext : " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());

        chain.doFilter(req, res); // Passer la requête à la chaîne de filtres suivante
    }


}
