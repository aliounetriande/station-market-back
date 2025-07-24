package com.stationmarket.api.vendor.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Pack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PackType packType;  // BASIC, STANDARD, PREMIUM

    private int maxMarketplaces; // Nombre maximal de Marketplaces pour ce pack

    // Vous pouvez ajouter d'autres informations spécifiques au pack, par exemple :
    private double price;

    private String description;  // Optionnel : description du pack
}
