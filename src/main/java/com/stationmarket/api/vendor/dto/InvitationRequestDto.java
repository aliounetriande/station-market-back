package com.stationmarket.api.vendor.dto;

import lombok.Data;

@Data
public class InvitationRequestDto {
    private String email;
    private String role; // "EDITOR" par exemple
    private String message; // message optionnel
}
