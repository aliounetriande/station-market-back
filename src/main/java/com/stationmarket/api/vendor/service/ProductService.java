package com.stationmarket.api.vendor.service;

import com.stationmarket.api.vendor.dto.ProductDto;
import com.stationmarket.api.vendor.model.Category;
import com.stationmarket.api.vendor.model.Marketplace;
import com.stationmarket.api.vendor.model.Product;
import com.stationmarket.api.vendor.repository.CategoryRepository;
import com.stationmarket.api.vendor.repository.MarketplaceRepository;
import com.stationmarket.api.vendor.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final CategoryRepository categoryRepository;
    private final MarketplaceRepository marketplaceRepository;
    private final ProductRepository productRepository;

    public ProductDto create(ProductDto dto) {
        Marketplace mp = marketplaceRepository.findBySlug(dto.getMarketplaceSlug())
                .orElseThrow(() -> new IllegalArgumentException("Marketplace introuvable"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable"));

        Product product = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .photo(dto.getPhoto()) // si c’est une URL ou valeur encodée
                .category(category)
                .marketplace(mp)
                .build();

        productRepository.save(product);
        return toDto(product);
    }

    public List<ProductDto> listByMarketplaceSlug(String slug) {
        Marketplace marketplace = marketplaceRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Marketplace introuvable"));

        List<Product> products = productRepository.findByMarketplaceId(marketplace.getId());
        return products.stream().map(this::toDto).collect(Collectors.toList());
    }

    private ProductDto toDto(Product p) {
        return ProductDto.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .photo(p.getPhoto())
                .categoryId(p.getCategory().getId())
                .marketplaceSlug(p.getMarketplace().getSlug())
                .build();
    }

}
