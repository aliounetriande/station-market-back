package com.stationmarket.api.vendor.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stationmarket.api.auth.model.Status;
import com.stationmarket.api.config.JsonConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@Setter
@Table(name = "marketplaces")
@Data @NoArgsConstructor @AllArgsConstructor
public class Marketplace {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String marketName;

    @Column(nullable = false, unique = true, columnDefinition = "TEXT")
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String shortDes;

    @Column(columnDefinition = "TEXT")
    private String photo; // bannière ou photo principale

    @Column(columnDefinition = "TEXT")
    private String logo;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String phone;

    @Enumerated(EnumType.STRING)
    private MarketplaceStatus status;

    @Column(columnDefinition = "TEXT")
    private String themeColor;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = JsonConverter.class)
    private Map<String, String> socialLinks = new HashMap<>();

    @Column(columnDefinition = "TEXT")
    private String openHours;

    private Boolean maintenanceMode = false;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;
}