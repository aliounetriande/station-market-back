package com.stationmarket.api.order.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "orders")
@Data @NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue
    private Long id;
    private String marketplaceSlug;
    private String status; // "PAID" pour validée
    private BigDecimal amount;
    private String userEmail;
    private Double deliveryLat;
    private Double deliveryLng;
    private String deliveryAddress;
    private String deliveryMode; // "DELIVERY" ou "PICKUP"
    private String transactionId; // ou le nom réel du champ
    private LocalDateTime createdAt;
}
