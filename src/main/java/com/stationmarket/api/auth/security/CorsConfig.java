package com.stationmarket.api.auth.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Permettre l'origine du frontend Angular et du domaine de production
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:4200", // Frontend local
                "http://localhost:8100", // Mobile Ionic
                "https://station-market.total-innovation.net" // Frontend en production
        ));

        // Permettre toutes les méthodes HTTP
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Permettre tous les headers (important pour Authorization)
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // Permettre les credentials (cookies, authorization headers, etc.)
        configuration.setAllowCredentials(true);

        // Exposer les headers de réponse
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        // Durée de cache pour les requêtes preflight
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
