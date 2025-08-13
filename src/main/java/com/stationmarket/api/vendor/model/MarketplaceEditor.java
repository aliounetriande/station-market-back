package com.stationmarket.api.vendor.model;

import com.stationmarket.api.auth.model.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "marketplace_editors")
@Data
public class MarketplaceEditor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marketplace_id", nullable = false)
    private Marketplace marketplace;

    @Column(nullable = false)
    private String role; // "EDITOR", "ADMIN", etc.
}
