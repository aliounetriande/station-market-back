package com.stationmarket.api.vendor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.stationmarket.api.vendor.model.Vendor;
import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByUserId(Long userId);
    Optional<Vendor> findByUserEmail(String email);
}
