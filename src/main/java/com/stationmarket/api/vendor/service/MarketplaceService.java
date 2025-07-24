package com.stationmarket.api.vendor.service;

import com.stationmarket.api.vendor.dto.MarketplaceDto;
import com.stationmarket.api.vendor.model.Marketplace;
import com.stationmarket.api.vendor.model.Vendor;

import java.util.List;
import java.util.Optional;

public interface MarketplaceService {
    Marketplace createMarketplace(MarketplaceDto dto, Vendor vendor);
    Marketplace updateMarketplace(Long marketplaceId, MarketplaceDto dto, Vendor vendor);
    Optional<Marketplace> getMarketplaceById(Long id);
    Optional<Marketplace> getMarketplaceByVendor(Vendor vendor);
    /** Récupère toutes les marketplaces d’un vendor */
    List<Marketplace> getMarketplacesByVendor(Vendor vendor);
}
