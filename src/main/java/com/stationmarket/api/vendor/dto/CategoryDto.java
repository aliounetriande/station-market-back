package com.stationmarket.api.vendor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    @NotBlank private Long id;
    @NotBlank private String name;
    @NotBlank private String marketplaceSlug;
}
