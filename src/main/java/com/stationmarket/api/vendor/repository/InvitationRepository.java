package com.stationmarket.api.vendor.repository;

import com.stationmarket.api.vendor.model.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    List<Invitation> findByMarketplaceSlugAndStatus(String marketplaceSlug, String status);
    Optional<Invitation> findByToken(String token);
    List<Invitation> findByStatusAndExpiresAtBefore(String status, LocalDateTime dateTime);
}
