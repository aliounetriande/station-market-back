package com.stationmarket.api.delivery.dto;

import lombok.Data;

@Data
public class CreateDeliveryAgentDto {
    private String name;
    private String email;
    private String phone;
    private String address;
    private String password;
    private Long marketplaceId;
}
