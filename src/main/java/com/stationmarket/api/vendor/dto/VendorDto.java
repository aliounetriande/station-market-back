package com.stationmarket.api.vendor.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class VendorDto {
    private Long id;
    private String email;
    private String name;
    private String password;
    private String phone;
    private String address;
    private String category;
    private Long packId;
}
