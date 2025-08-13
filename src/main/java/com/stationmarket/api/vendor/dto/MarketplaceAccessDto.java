package com.stationmarket.api.vendor.dto;

import lombok.Data;

@Data
public class MarketplaceAccessDto {
    private Long id;
    private String marketName;
    private String slug;
    private String logo;
    private String role; // "OWNER" ou "EDITOR"

    // constructeurs, getters, setters
    public MarketplaceAccessDto(Long id, String marketName, String slug, String logo, String role) {
        this.id = id;
        this.marketName = marketName;
        this.slug = slug;
        this.logo = logo;
        this.role = role;
    }

    // getters et setters...
}

