package com.stationmarket.api.auth.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Data @NoArgsConstructor @AllArgsConstructor
public class Role {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, unique = true, nullable = false)
    private RoleName name;

    public enum RoleName {
        ROLE_ADMIN,
        ROLE_VENDOR,
        ROLE_DELIVERY,
        ROLE_CUSTOMER
    }
}