package com.stationmarket.api.vendor.dto;

import lombok.Data;

@Data
public class UserMarketplacePermissions {
    private String marketplaceSlug;
    private Long userId;
    private String role; // OWNER, EDITOR
    private boolean canManageProducts;
    private boolean canManageCategories;
    private boolean canManageOrders;
    private boolean canManageSettings;
    private boolean canInviteUsers;
    private boolean canViewAnalytics;

    // getters et setters...
}
