package com.stationmarket.api.vendor.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InvitationDto {
    private Long id;
    private String email;
    private String role;
    private String status; // "PENDING", "ACCEPTED", "CANCELLED"
    private String token;
    private Long marketplaceId;
    private String marketplaceSlug;
    private String inviterName;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
