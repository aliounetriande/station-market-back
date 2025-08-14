package com.stationmarket.api.payments.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Data
@Component
@ConfigurationProperties(prefix = "ligdicash")
public class LigdicashProperties {

    private Api api = new Api();
    private Store store = new Store();
    private Urls urls = new Urls();

    @Data
    public static class Api {
        private String baseUrl;
        private String apiKey;
        private String apiToken;
    }

    @Data
    public static class Store {
        private String name;
        private String websiteUrl;
    }

    @Data
    public static class Urls {
        private String success;
        private String cancel;
        private String callback;
    }

    // ✅ Ajouter ce bean RestTemplate
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
