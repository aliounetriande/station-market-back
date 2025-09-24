package com.stationmarket.api.customer.model;

import com.stationmarket.api.auth.model.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customers")
@Data @NoArgsConstructor @AllArgsConstructor
public class Customer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    private String phoneNumb;
    private String address;
}

