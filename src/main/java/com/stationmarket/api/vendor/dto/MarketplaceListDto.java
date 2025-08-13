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
    @NotBlank private String role; // "OWNER" ou "EDITOR"


    // Constructeur supplémentaire pour la projection JPQL
    public MarketplaceListDto(Long id, String marketName, String slug, String logo) {
        this.id = id;
        this.marketName = marketName;
        this.slug = slug;
        this.logo = logo;
        this.role = "OWNER"; // Valeur par défaut
    }
}
