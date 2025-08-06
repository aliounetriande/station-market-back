package com.stationmarket.api.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * On monte un DaoAuthenticationProvider pour lier UserDetailsService + PasswordEncoder.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    /**
     * Permet d’injecter AuthenticationManager (au besoin dans ton AuthService).
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configuration principale des filtres Spring Security.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Filtre JWT personnalisé
        var jwtFilter = new JwtAuthenticationFilter(jwtUtils, userDetailsService);


        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // 1. Pas de CSRF, on est en API stateless
                .csrf(csrf -> csrf.disable())

                // 2. Pas de session, on gère tout via token
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 3. Règles d’accès
                .authorizeHttpRequests(authz -> authz
                        // seuls ces endpoints sont publics

                        // 1) PUBLIC : accès aux images uploadées
                        .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

                        // 3) AUTH pour l’auth & swagger
                        .requestMatchers(
                                "/stationmarket/auth/login",
                                "/stationmarket/auth/register",
                                "/stationmarket/auth/confirm"
                        ).permitAll()
                        // swagger-ui reste accessible sans authentification
                        .requestMatchers("/stationmarket/swagger-ui/**").permitAll()

                        // /profile exige un JWT valide (auth ne sera plus null)
                        .requestMatchers("/stationmarket/auth/profile").authenticated()

                        // accès restreint aux utilisateurs avec le rôle ROLE_VENDOR
                        .requestMatchers("/stationmarket/vendor/marketplaces/*").hasAuthority("ROLE_VENDOR")
                        .requestMatchers("/api/vendors/setup").hasAuthority("ROLE_VENDOR")
                        .requestMatchers("/api/vendors/me"   ).hasAuthority("ROLE_VENDOR")

                        // tout le reste exige authentification
                        .anyRequest().authenticated()
                )


                // 4. On ajoute notre provider et notre filtre JWT
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
