package com.stationmarket.api.common.dto;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data @NoArgsConstructor @AllArgsConstructor
public class SignupRequest {
    private String name;
    private String email;
    private String password;
    private Set<String> roles= new HashSet<>();  // <- initialisé
}
