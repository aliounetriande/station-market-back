package com.stationmarket.api.payments.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LigdicashInvoiceResponse {

    @JsonProperty("response_code")
    private String responseCode;

    @JsonProperty("response_text")
    private String responseText;

    private String token;
    private String description;

    @JsonProperty("custom_data")
    private JsonNode customData;

    private String wiki;
}
