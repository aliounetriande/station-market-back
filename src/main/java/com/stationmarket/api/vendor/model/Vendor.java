package com.stationmarket.api.vendor.model;

import com.stationmarket.api.auth.model.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@Table(name = "vendors")
@Data @NoArgsConstructor @AllArgsConstructor
public class Vendor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** chaque Vendor *est* un User */
    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @ManyToOne
    @JoinColumn(name = "pack_id")
    private Pack pack;  // Le pack associé au vendeur

    private String phone;
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VendorCategory category;   // ← passe en enum

    @OneToOne(mappedBy = "vendor")
    private Marketplace marketplace;
}
