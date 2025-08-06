package com.stationmarket.api.vendor.repository;

import com.stationmarket.api.vendor.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

    public interface ProductRepository extends JpaRepository<Product, Long> {
        List<Product> findByMarketplace_Slug(String slug);

        List<Product> findByMarketplaceId(Long marketplaceId);

        long count();

        @Query("SELECT COUNT(p) FROM Product p WHERE p.marketplace.slug = :slug")
        Long countByMarketplaceSlug(@Param("slug") String slug);


    }

