package com.stationmarket.api.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    //La methode ci dessous est inutile car le CORS est géré dans CorsConfig
    //@Override
    //public void addCorsMappings(CorsRegistry registry) {
        //registry.addMapping("/**")
      //          .allowedOrigins(
     //                   "http://localhost:4200", // Frontend local
    //                    "https://station-market.total-innovation.net" // Frontend en production
    //            )
    //            .allowedMethods("GET", "POST", "PUT", "DELETE") // Autoriser les méthodes nécessaires
    //            .allowedHeaders("*") // Autoriser tous les en-têtes
      //          .allowCredentials(true); // Activer les credentials si nécessaire
   // }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/api/products/photo/**")
                .addResourceLocations("file:uploads/")
                .setCachePeriod(3600);
    }
}