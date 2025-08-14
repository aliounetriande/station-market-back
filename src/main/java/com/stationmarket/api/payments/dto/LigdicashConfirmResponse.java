package com.stationmarket.api.payments.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LigdicashConfirmResponse {

    private String date;

    @JsonProperty("response_code")
    private String responseCode;

    private String token;
    private String description;
    private String amount;
    private String montant;

    @JsonProperty("response_text")
    private String responseText;

    private String status;

    @JsonProperty("custom_data")
    private List<CustomData> customData;

    @JsonProperty("operator_name")
    private String operatorName;

    @JsonProperty("operator_id")
    private String operatorId;

    private String customer;

    @JsonProperty("transaction_id")
    private String transactionId;

    @JsonProperty("external_id")
    private String externalId;

    @JsonProperty("request_id")
    private String requestId;

    private String wiki;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomData {
        @JsonProperty("id_invoice")
        private Long idInvoice;

        @JsonProperty("keyof_customdata")
        private String keyOfCustomData;

        @JsonProperty("valueof_customdata")
        private String valueOfCustomData;
    }
}
