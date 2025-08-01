package com.stationmarket.api.vendor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.stationmarket.api.vendor.model.Vendor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VendorRepository extends JpaRepository<Vendor, Long> {
    Optional<Vendor> findByUserId(Long userId);
    @Query("select distinct v.id from Vendor v join v.user u where u.email = :email")
    Optional<Long> findIdByUserEmail(@Param("email") String email);
}
