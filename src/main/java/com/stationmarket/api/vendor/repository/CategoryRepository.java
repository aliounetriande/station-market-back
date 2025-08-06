package com.stationmarket.api.vendor.repository;

import com.stationmarket.api.vendor.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

//public interface CategoryRepository extends JpaRepository<Category, Long> {
  //  List<Category> findByMarketplaceId(Long marketplaceId);
//}

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE c.marketplace.slug = :slug")
    List<Category> findByMarketplace_Slug(String slug);

    long count();

    @Query("SELECT COUNT(c) FROM Category c WHERE c.marketplace.slug = :slug")
    Long countByMarketplaceSlug(@Param("slug") String slug);
}
