package com.stationmarket.api.order.repository;

import com.stationmarket.api.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByMarketplaceSlugAndStatus(String slug, String status);

    @Query("SELECT COALESCE(SUM(o.amount), 0) FROM Order o WHERE o.marketplaceSlug = :slug AND o.status = :status")
    BigDecimal sumAmountByMarketplaceSlugAndStatus(@Param("slug") String slug, @Param("status") String status);
}
