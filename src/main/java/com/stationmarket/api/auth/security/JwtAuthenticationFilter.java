package com.stationmarket.api.auth.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        // ✅ CORRECTION : Endpoints PUBLICS spécifiques seulement
        boolean shouldSkip =
                // Endpoints publics d'authentification
                path.equals("/stationmarket/auth/login") ||
                        path.equals("/stationmarket/auth/register") ||
                        path.equals("/stationmarket/auth/confirm") ||

                        // Autres endpoints publics
                        path.startsWith("/stationmarket/swagger-ui/") ||
                        path.startsWith("/api/products/photo/") ||
                        path.startsWith("/uploads/") ||
                        path.equals("/error") ||

                        // Endpoints d'invitation publics
                        path.startsWith("/api/invitations/validate/") ||
                        path.equals("/api/invitations/accept-complete") ||
                        path.equals("/api/invitations/accept");

        if (shouldSkip) {
            log.info("✅ [JWT FILTER] SKIP pour endpoint public: {}", path);
        } else {
            log.info("🔒 [JWT FILTER] Traitement JWT pour: {}", path);
        }

        return shouldSkip;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                    FilterChain chain) throws ServletException, java.io.IOException {

        String path = req.getServletPath();
        log.info("🔍 [JWT FILTER] Processing protected endpoint: {}", path);

        String header = req.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (jwtUtils.validateJwtToken(token)) {
                try {
                    String email = jwtUtils.getEmailFromToken(token);

                    // Chargez les rôles à partir du JWT
                    List<GrantedAuthority> authorities = jwtUtils.getRolesFromToken(token);

                    // Chargez l'utilisateur à partir du CustomUserDetailsService
                    UserDetails uds = userDetailsService.loadUserByUsername(email);

                    log.info("✅ [JWT FILTER] Roles dans UserDetails: {}", uds.getAuthorities());
                    log.info("✅ [JWT FILTER] Roles extraits du JWT: {}", authorities);

                    // Créez un token d'authentification avec les rôles du JWT
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            uds, null, authorities);

                    // Définissez l'authentification dans le contexte de sécurité
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.info("✅ [JWT FILTER] Authentification réussie pour: {}", email);

                } catch (Exception e) {
                    log.error("❌ [JWT FILTER] Erreur lors de l'authentification: ", e);
                    SecurityContextHolder.clearContext();
                }
            } else {
                log.warn("⚠️ [JWT FILTER] Token JWT invalide");
                SecurityContextHolder.clearContext();
            }
        } else {
            log.warn("⚠️ [JWT FILTER] Pas de token Authorization Bearer trouvé");
        }

        // ✅ CORRECTION CRITIQUE : Vérifier que l'authentication existe avant d'accéder aux authorities
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null) {
            log.info("✅ [JWT FILTER] Roles finaux dans le SecurityContext: {}", authentication.getAuthorities());
        } else {
            log.info("ℹ️ [JWT FILTER] Aucune authentification dans le SecurityContext (normal pour endpoints protégés sans token)");
        }

        chain.doFilter(req, res);
    }
}