package com.stationmarket.api.vendor.dto;

import com.stationmarket.api.vendor.model.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    @NotNull private Long id;
    @NotBlank private String name;
    @NotBlank private String description;
    @NotNull private BigDecimal price;
    @NotBlank private String photo; // À corriger si c’est une URL ou une image encodée
    @NotNull private Long categoryId;
    @NotBlank private String marketplaceSlug;

    /** Crée un ProductDto à partir d’une entité Product */
    public static ProductDto fromEntity(Product p) {
        return ProductDto.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .photo(p.getPhoto())             // filename ou URL
                .categoryId(p.getCategory().getId())
                .marketplaceSlug(p.getMarketplace().getSlug())
                .build();
    }

    /** Si besoin, l’inverse pour enregistrer depuis un DTO */
    public Product toEntity() {
        Product p = new Product();
        p.setId(this.id);
        p.setName(this.name);
        p.setDescription(this.description);
        p.setPrice(this.price);
        p.setPhoto(this.photo);
        // categories / marketplaces à gérer dans le service
        return p;
    }
}

