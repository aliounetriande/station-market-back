package com.stationmarket.api.vendor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stationmarket.api.vendor.dto.ProductDto;
import com.stationmarket.api.vendor.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser  // ✅ AJOUT ICI
    void testListByMarketplace() throws Exception {
        // Données de test
        String marketplaceSlug = "test-marketplace";
        List<ProductDto> products = Arrays.asList(
                ProductDto.builder().id(1L).name("Product 1").price(BigDecimal.valueOf(10.00)).build(),
                ProductDto.builder().id(2L).name("Product 2").price(BigDecimal.valueOf(20.00)).build()
        );

        // Mock du service
        when(productService.listByMarketplaceSlug(marketplaceSlug)).thenReturn(products);

        // Appel de l'endpoint
        mockMvc.perform(get("/api/products/by-marketplace/{slug}", marketplaceSlug)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Product 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Product 2"));

        // Vérification que le service a été appelé une fois
        verify(productService, times(1)).listByMarketplaceSlug(marketplaceSlug);
    }

    @Test
    @WithMockUser(authorities = "ROLE_VENDOR")
    void testCreateProduct() throws Exception {
        // Données de test
        ProductDto inputDto = ProductDto.builder()
                .name("New Product")
                .description("Product description")
                .price(BigDecimal.valueOf(99.99))
                .categoryId(1L)
                .marketplaceSlug("test-marketplace")
                .build();

        ProductDto outputDto = ProductDto.builder()
                .id(1L)
                .name("New Product")
                .description("Product description")
                .price(BigDecimal.valueOf(99.99))
                .categoryId(1L)
                .marketplaceSlug("test-marketplace")
                .build();

        // Mock du service
        when(productService.create(any(ProductDto.class))).thenReturn(outputDto);

        // Appel de l'endpoint
        mockMvc.perform(post("/api/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("New Product"))
                .andExpect(jsonPath("$.price").value(99.99));

        verify(productService, times(1)).create(any(ProductDto.class));
    }

    @Test
    @WithMockUser(authorities = "ROLE_VENDOR")
    void testUpdateProduct() throws Exception {
        // Données de test
        Long productId = 1L;
        ProductDto inputDto = ProductDto.builder()
                .name("Updated Product")
                .description("Updated description")
                .price(BigDecimal.valueOf(150.00))
                .categoryId(2L)
                .marketplaceSlug("updated-marketplace")
                .build();

        ProductDto outputDto = ProductDto.builder()
                .id(productId)
                .name("Updated Product")
                .description("Updated description")
                .price(BigDecimal.valueOf(150.00))
                .categoryId(2L)
                .marketplaceSlug("updated-marketplace")
                .build();

        // Mock du service
        when(productService.update(any(ProductDto.class))).thenReturn(outputDto);

        // Appel de l'endpoint
        mockMvc.perform(put("/api/products/{id}", productId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.price").value(150.00));

        verify(productService, times(1)).update(any(ProductDto.class));
    }

    @Test
    @WithMockUser(authorities = "ROLE_VENDOR")
    void testDeleteProduct() throws Exception {
        // Mock du service
        doNothing().when(productService).delete(1L);

        // Appel de l'endpoint
        mockMvc.perform(delete("/api/products/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(productService, times(1)).delete(1L);
    }

    @Test
    @WithMockUser(authorities = "ROLE_VENDOR")
    void testCountProducts() throws Exception {
        // Mock du service
        when(productService.countAllProducts()).thenReturn(25L);

        // Appel de l'endpoint
        mockMvc.perform(get("/api/products/count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("25"));

        verify(productService, times(1)).countAllProducts();
    }

    @Test
    @WithMockUser(authorities = "ROLE_VENDOR")
    void testCountProductsByMarketplace() throws Exception {
        // Mock du service
        when(productService.countByMarketplaceSlug("test-marketplace")).thenReturn(10L);

        // Appel de l'endpoint
        mockMvc.perform(get("/api/products/count/by-marketplace/{slug}", "test-marketplace")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));

        verify(productService, times(1)).countByMarketplaceSlug("test-marketplace");
    }
}