package com.stationmarket.api.delivery.dto;

import lombok.Data;

@Data
public class UpdateDeliveryAgentDto {
    private String name;
    private String email;
    private String phone;
    private String address;
    private String password; // optionnel, si modifié
}
