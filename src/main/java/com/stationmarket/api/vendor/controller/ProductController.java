package com.stationmarket.api.vendor.controller;

import com.stationmarket.api.vendor.dto.ProductDto;
import com.stationmarket.api.vendor.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService ps) {
        this.productService = ps;
    }

    @PostMapping
    public ProductDto create(@RequestBody ProductDto dto) {
        return productService.create(dto);
    }

    @PutMapping("/{id}")
    public ProductDto update(@PathVariable Long id, @RequestBody ProductDto dto) {
        dto.setId(id);
        return productService.update(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }

    @GetMapping("/by-marketplace/{slug}")
    public ResponseEntity<List<ProductDto>> listByMarketplace(@PathVariable String slug) {
        List<ProductDto> list = productService.listByMarketplaceSlug(slug);
        return ResponseEntity.ok(list);
    }
}
