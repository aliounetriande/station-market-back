package com.stationmarket.api.vendor.service;

import com.stationmarket.api.vendor.dto.ProductDto;
import com.stationmarket.api.vendor.model.Category;
import com.stationmarket.api.vendor.model.Marketplace;
import com.stationmarket.api.vendor.model.Product;
import com.stationmarket.api.vendor.repository.CategoryRepository;
import com.stationmarket.api.vendor.repository.MarketplaceRepository;
import com.stationmarket.api.vendor.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private MarketplaceRepository marketplaceRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void testCreateProduct() {
        // Données de test
        ProductDto productDto = ProductDto.builder()
                .name("Test Product")
                .description("Description du produit")
                .price(BigDecimal.valueOf(100.50))
                .photo("photo.jpg")
                .categoryId(1L)
                .marketplaceSlug("test-marketplace")
                .build();

        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        Marketplace marketplace = new Marketplace();
        marketplace.setId(1L);
        marketplace.setSlug("test-marketplace");
        marketplace.setEmail("test@example.com");
        marketplace.setMarketName("Test Marketplace"); // Important: champ obligatoire

        Product savedProduct = Product.builder()
                .id(1L) // Important: ajouter l'ID pour le produit sauvegardé
                .name("Test Product")
                .description("Description du produit")
                .price(BigDecimal.valueOf(100.50))
                .photo("photo.jpg")
                .category(category)
                .marketplace(marketplace)
                .build();

        // Mocks
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(marketplaceRepository.findBySlug("test-marketplace")).thenReturn(Optional.of(marketplace));
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // Appel de la méthode
        ProductDto result = productService.create(productDto);

        // Vérifications
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        assertEquals("Description du produit", result.getDescription());
        assertEquals(0, result.getPrice().compareTo(BigDecimal.valueOf(100.50)));
        assertEquals("photo.jpg", result.getPhoto());
        assertEquals(1L, result.getCategoryId());
        assertEquals("test-marketplace", result.getMarketplaceSlug());

        // Vérifie que les méthodes des mocks ont été appelées
        verify(categoryRepository, times(1)).findById(1L);
        verify(marketplaceRepository, times(1)).findBySlug("test-marketplace");
        verify(productRepository, times(1)).save(any(Product.class));
    }
}