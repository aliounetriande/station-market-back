package com.stationmarket.api.common.dto;

import lombok.*;
import java.util.List;

@Data @AllArgsConstructor @Builder
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String name;
    private String email;
    private List<String> roles;
}
