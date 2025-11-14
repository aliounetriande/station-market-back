package com.stationmarket.api.vendor.repository;

import com.stationmarket.api.auth.model.Status;
import com.stationmarket.api.auth.model.User;
import com.stationmarket.api.auth.repository.UserRepository;
import com.stationmarket.api.vendor.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MarketplaceRepository marketplaceRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testCountByMarketplaceSlug() {
        // 1. Créer un utilisateur
        User user = User.builder()
                .name("Test Vendor")
                .email("vendor@test.com")
                .password("password123")
                .status(Status.ACTIVE)
                .profileCompleted(true)
                .build();
        user = userRepository.save(user);

        // 2. Créer un vendor
        Vendor vendor = Vendor.builder()
                .user(user)
                .category(VendorCategory.Station)
                .phone("0123456789")
                .address("123 Test Street")
                .build();
        vendor = vendorRepository.save(vendor);

        // 3. Créer un marketplace
        Marketplace marketplace = new Marketplace();
        marketplace.setSlug("test-marketplace");
        marketplace.setEmail("marketplace@test.com");
        marketplace.setMarketName("Test Marketplace");
        marketplace.setVendor(vendor);
        //marketplace.setStatus(MarketplaceStatus.ACTIVE);
        marketplace = marketplaceRepository.save(marketplace);

        // 4. Créer une catégorie
        Category category = new Category();
        category.setName("Test Category");
        category.setMarketplace(marketplace);
        category = categoryRepository.save(category);

        // 5. Créer des produits
        Product product1 = Product.builder()
                .name("Product 1")
                .description("Description 1")
                .price(BigDecimal.valueOf(10.00))
                .marketplace(marketplace)
                .category(category)
                .build();

        Product product2 = Product.builder()
                .name("Product 2")
                .description("Description 2")
                .price(BigDecimal.valueOf(20.00))
                .marketplace(marketplace)
                .category(category)
                .build();

        productRepository.saveAll(List.of(product1, product2));

        // 6. Appel de la méthode
        Long count = productRepository.countByMarketplaceSlug("test-marketplace");

        // 7. Vérifications
        assertThat(count).isEqualTo(2L);
    }

    @Test
    void testFindByMarketplaceSlug() {
        // 1. Créer un utilisateur
        User user = User.builder()
                .name("Test Vendor 2")
                .email("vendor2@test.com")
                .password("password123")
                .status(Status.ACTIVE)
                .profileCompleted(true)
                .build();
        user = userRepository.save(user);

        // 2. Créer un vendor
        Vendor vendor = Vendor.builder()
                .user(user)
                .category(VendorCategory.Alimentation)
                .phone("0987654321")
                .address("456 Test Avenue")
                .build();
        vendor = vendorRepository.save(vendor);

        // 3. Créer un marketplace
        Marketplace marketplace = new Marketplace();
        marketplace.setSlug("marketplace-2");
        marketplace.setEmail("marketplace2@test.com");
        marketplace.setMarketName("Marketplace 2");
        marketplace.setVendor(vendor);
        //marketplace.setStatus(MarketplaceStatus.ACTIVE);
        marketplace = marketplaceRepository.save(marketplace);

        // 4. Créer une catégorie
        Category category = new Category();
        category.setName("Category 2");
        category.setMarketplace(marketplace);
        category = categoryRepository.save(category);

        // 5. Créer un produit
        Product product = Product.builder()
                .name("Product Test")
                .description("Test Description")
                .price(BigDecimal.valueOf(15.99))
                .marketplace(marketplace)
                .category(category)
                .build();

        productRepository.save(product);

        // 6. Appel de la méthode
        List<Product> products = productRepository.findByMarketplace_Slug("marketplace-2");

        // 7. Vérifications
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("Product Test");
        assertThat(products.get(0).getPrice()).isEqualByComparingTo(BigDecimal.valueOf(15.99));
    }


}