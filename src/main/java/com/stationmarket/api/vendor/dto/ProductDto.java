package com.stationmarket.api.vendor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    @NotNull private Long id;
    @NotBlank private String name;
    @NotBlank private String description;
    @NotNull private BigDecimal price;
    @NotBlank private String photo; // À corriger si c’est une URL ou une image encodée
    @NotNull private Long categoryId;
    @NotBlank private String marketplaceSlug;
}
