package com.stationmarket.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "file:.env", ignoreResourceNotFound = true)
public class EnvironmentConfig {
    // Cette classe charge automatiquement le fichier .env
}