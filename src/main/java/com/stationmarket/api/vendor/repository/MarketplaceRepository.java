package com.stationmarket.api.vendor.repository;

import com.stationmarket.api.vendor.dto.MarketplaceListDto;
import com.stationmarket.api.vendor.model.Marketplace;
import com.stationmarket.api.vendor.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MarketplaceRepository extends JpaRepository<Marketplace, Long> {

    @Query("""
      select distinct new com.stationmarket.api.vendor.dto.MarketplaceListDto(
        m.id,
        m.marketName,
        m.slug,
        m.logo
      )
      from Marketplace m
      where m.vendor.id = :vendorId
    """)
    List<MarketplaceListDto> findAllDtoByVendorId(@Param("vendorId") Long vendorId);

    boolean existsBySlug(String slug);
    Optional<Marketplace> findBySlug(String slug);

    Optional<Marketplace> findByVendor(Vendor vendor);
    // Pour plusieurs marketplaces (packs Standard & Premium)
    //List<Marketplace> findAllByVendor(Vendor vendor);
    boolean existsByVendor(Vendor vendor);

    // Ajoutez la méthode countByVendor pour compter les Marketplaces d'un Vendor
    long countByVendor(Vendor vendor);
}
