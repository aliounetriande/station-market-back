package com.stationmarket.api.vendor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class AcceptInvitationRequestDto {
    private String token;
    private String password; // Si l'utilisateur doit créer un compte
    private String fullName;
    private String phone; // Si l'utilisateur doit créer un compte
    private String address; // Si l'utilisateur doit créer un compte
}
