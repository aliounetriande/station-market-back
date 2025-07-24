package com.stationmarket.api.vendor.repository;

import com.stationmarket.api.vendor.model.Marketplace;
import com.stationmarket.api.vendor.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MarketplaceRepository extends JpaRepository<Marketplace, Long> {
    Optional<Marketplace> findByVendor(Vendor vendor);
    // Pour plusieurs marketplaces (packs Standard & Premium)
    List<Marketplace> findAllByVendor(Vendor vendor);
    boolean existsByVendor(Vendor vendor);

    // Ajoutez la méthode countByVendor pour compter les Marketplaces d'un Vendor
    long countByVendor(Vendor vendor);
}
