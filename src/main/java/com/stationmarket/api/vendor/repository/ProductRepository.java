package com.stationmarket.api.vendor.repository;

import com.stationmarket.api.vendor.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

    public interface ProductRepository extends JpaRepository<Product, Long> {
        List<Product> findByMarketplace_Slug(String slug);

        List<Product> findByMarketplaceId(Long marketplaceId);

    }

