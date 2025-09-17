package com.stationmarket.api.delivery.repository;

import com.stationmarket.api.delivery.model.DeliveryAgent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryAgentRepository extends JpaRepository<DeliveryAgent, Long> {
    List<DeliveryAgent> findByMarketplaceId(Long marketplaceId);
    Optional<DeliveryAgent> findByUserId(Long userId);
    Optional<DeliveryAgent> findByUserEmail(String email);
}
