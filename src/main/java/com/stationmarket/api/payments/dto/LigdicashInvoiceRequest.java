package com.stationmarket.api.payments.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LigdicashInvoiceRequest {

    // ✅ Structure directe selon la doc Ligdicash
    private Invoice invoice;
    private Store store;
    private Actions actions;
    @JsonProperty("custom_data")
    private Map<String, Object> customData;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Invoice {
        private List<Item> items;
        @JsonProperty("total_amount")
        private Integer totalAmount;
        private String devise; // = "XOF"
        private String description;
        private String customer; // = ""
        @JsonProperty("customer_firstname")
        private String customerFirstname;
        @JsonProperty("customer_lastname")
        private String customerLastname;
        @JsonProperty("customer_email")
        private String customerEmail;
        @JsonProperty("external_id")
        private String externalId; // = ""
        private String otp; // = ""
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private String name;
        private String description;
        private Integer quantity;
        @JsonProperty("unit_price")
        private Integer unitPrice;
        @JsonProperty("total_price")
        private Integer totalPrice;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Store {
        private String name;
        @JsonProperty("website_url")
        private String websiteUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Actions {
        @JsonProperty("cancel_url")
        private String cancelUrl;
        @JsonProperty("return_url")
        private String returnUrl;
        @JsonProperty("callback_url")
        private String callbackUrl;
    }
}