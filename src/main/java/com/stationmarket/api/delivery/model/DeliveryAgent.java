package com.stationmarket.api.delivery.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stationmarket.api.auth.model.User;
import com.stationmarket.api.vendor.model.Marketplace;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "delivery_agents")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DeliveryAgent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "marketplace_id", nullable = false)
    private Marketplace marketplace;
}
