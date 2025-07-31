package com.stationmarket.api.vendor.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id @GeneratedValue
    private Long id;

    private String name;
    private String description;
    private BigDecimal price;
    private String photo;
    @ManyToOne
    private Category category;

    @ManyToOne
    private Marketplace marketplace;

}
