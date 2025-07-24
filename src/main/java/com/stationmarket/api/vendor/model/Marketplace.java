package com.stationmarket.api.vendor.model;

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

    @Column(nullable = false)
    private String marketName;

    @Column(length = 255)
    private String shortDes;

    @Lob
    private String description;

    private String photo; // bannière ou photo principale

    private String logo;

    @Column(length = 255)
    private String address;

    @Column(unique = true, nullable = false)
    private String email;

    private String phone;

    @Enumerated(EnumType.STRING)
    private MarketplaceStatus status;

    private String themeColor;

    @Convert(converter = JsonConverter.class)
    private Map<String, String> socialLinks = new HashMap<>();

    private String openHours;

    private Boolean maintenanceMode = false;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor;
}