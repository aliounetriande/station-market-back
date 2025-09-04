package com.stationmarket.api.withdrawal.repository;

import com.stationmarket.api.withdrawal.model.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {
    List<Withdrawal> findByVendorEmail(String vendorEmail);
    List<Withdrawal> findByMarketplaceSlug(String marketplaceSlug);
    long countByStatus(String status);
    List<Withdrawal> findByStatus(String status);

    @Query("SELECT SUM(w.amount) FROM Withdrawal w WHERE w.marketplaceSlug = :marketplaceSlug AND w.status = :status")
    BigDecimal sumAmountByMarketplaceSlugAndStatus(String marketplaceSlug, String status);
}
