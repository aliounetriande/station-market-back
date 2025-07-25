package com.stationmarket.api.vendor.dto;
import com.stationmarket.api.vendor.model.MarketplaceStatus;
import lombok.*;

import jakarta.validation.constraints.*;

import java.util.Map;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MarketplaceCreateDto {
    @NotBlank private String marketName;
    @NotBlank  private String logo;
    @NotBlank  private String address;
    @NotBlank  private String email;
    @NotBlank private String phone;
    @NotBlank   private String slug;
    @NotBlank  private String themeColor;
    @NotBlank   private Map<String, String> socialLinks;
    @NotBlank  private String openHours;
}
