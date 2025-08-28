package com.stationmarket.api.payments.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentIntent {
    @Id
    private String orderId; // correspond à order_id envoyé à Ligdicash
    private String marketplaceSlug;
    private String userEmail;
    private Integer amount;
    private String status; // "PENDING", "PAID", etc.
}