package com.stationmarket.api.vendor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketplaceListDto {
    @NotNull private Long   id;
    @NotBlank private String marketName;
    @NotBlank private String slug;
    @NotBlank private String logo;
}
