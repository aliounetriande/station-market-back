package com.stationmarket.api.common.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class LoginRequest {
    private String email;
    private String password;
}
