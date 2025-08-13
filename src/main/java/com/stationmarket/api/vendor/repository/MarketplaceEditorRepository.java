package com.stationmarket.api.vendor.repository;

import com.stationmarket.api.vendor.model.MarketplaceEditor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarketplaceEditorRepository extends JpaRepository<MarketplaceEditor, Long> {

    @Query("SELECT me FROM MarketplaceEditor me WHERE me.user.id = :userId AND me.marketplace.id = :marketplaceId")
    Optional<MarketplaceEditor> findByUserIdAndMarketplaceId(@Param("userId") Long userId, @Param("marketplaceId") Long marketplaceId);

    @Query("SELECT me FROM MarketplaceEditor me WHERE me.user.email = :email AND me.marketplace.slug = :slug")
    Optional<MarketplaceEditor> findByUserEmailAndMarketplaceSlug(@Param("email") String email, @Param("slug") String slug);

    @Query("SELECT me FROM MarketplaceEditor me WHERE me.marketplace.id = :marketplaceId")
    List<MarketplaceEditor> findByMarketplaceId(@Param("marketplaceId") Long marketplaceId);

    @Query("SELECT me FROM MarketplaceEditor me WHERE me.user.id = :userId")
    List<MarketplaceEditor> findByUserId(@Param("userId") Long userId);
}