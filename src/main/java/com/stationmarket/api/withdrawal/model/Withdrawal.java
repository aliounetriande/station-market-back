package com.stationmarket.api.withdrawal.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "withdrawals")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Withdrawal {
    @Id
    @GeneratedValue
    private Long id;
    private String vendorEmail;         // <-- Utilise l'email du vendeur
    private String marketplaceSlug;     // Lien avec la marketplace
    private BigDecimal amount;
    private String status;              // PENDING, PAID, REJECTED
    private String paymentMethod; // "BANK", "OM", "MOOV"
    private String bankName;
    private String bankAccountNumber;
    private String bankAccountHolder;
    private String mobileOperator; // "OM" ou "MOOV"
    private String mobileNumber;
    private String accountHolder;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
}
