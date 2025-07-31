package com.stationmarket.api.vendor.controller;

import com.stationmarket.api.vendor.dto.CategoryDto;
import com.stationmarket.api.vendor.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

//    @PostMapping
//    public CategoryDto create(@RequestParam("marketplaceSlug") String slug,
//                              @RequestParam("name") String name) {
//        return categoryService.create(slug, name);
//    }

    @PostMapping
    public CategoryDto create(@RequestBody CategoryDto dto) {
        return categoryService.create(dto.getMarketplaceSlug(), dto.getName());
    }

    @GetMapping("/by-slug/{slug}")
    public List<CategoryDto> listBySlug(@PathVariable String slug) {
        return categoryService.listBySlug(slug);
    }
}
