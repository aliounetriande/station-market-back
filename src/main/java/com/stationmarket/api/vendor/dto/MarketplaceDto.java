package com.stationmarket.api.vendor.dto;

import lombok.Data;
import com.stationmarket.api.vendor.model.MarketplaceStatus;

import java.util.Map;

@Data
public class MarketplaceDto {
    private Long id;
    private String marketName;
    private String shortDes;
    private String photo;
    private String logo;
    private String address;
    private String email;
    private String phone;
    private MarketplaceStatus status;   // ex. OPEN, CLOSED, MAINTENANCE
    private String slug;
    private String themeColor;
    private Map<String, String> socialLinks;
    private String openHours;
    private Boolean maintenanceMode;
}
