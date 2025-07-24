package com.stationmarket.api.vendor.repository;

import com.stationmarket.api.vendor.model.Pack;
import com.stationmarket.api.vendor.model.PackType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PackRepository extends JpaRepository<Pack, Long> {
    Optional<Pack> findByPackType(PackType packType);
}
