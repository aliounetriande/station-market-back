package com.stationmarket.api.vendor.service;

import com.stationmarket.api.vendor.dto.CategoryDto;
import com.stationmarket.api.vendor.model.Category;
import com.stationmarket.api.vendor.model.Marketplace;
import com.stationmarket.api.vendor.repository.CategoryRepository;
import com.stationmarket.api.vendor.repository.MarketplaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final MarketplaceRepository marketplaceRepository;

    public CategoryDto create(String slug, String name) {

        Marketplace mp = marketplaceRepository.findBySlug(slug)

                .orElseThrow(() -> new IllegalArgumentException("Marketplace introuvable"));
        Category cat = Category.builder()
                .name(name)
                .marketplace(mp)
                .build();
        categoryRepository.save(cat);
        return toDto(cat);

    }

    public CategoryDto update(Long id, String name) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable"));

        category.setName(name);
        categoryRepository.save(category);
        return toDto(category);
    }

    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable"));

        categoryRepository.delete(category);
    }

    public long countAllCategories() {
        return categoryRepository.count();
    }

    public Long countByMarketplaceSlug(String slug) {
        return categoryRepository.countByMarketplaceSlug(slug);
    }


    public List<CategoryDto> listBySlug(String slug) {
        List<Category> list = categoryRepository.findByMarketplace_Slug(slug);
        System.out.println("slug = " + slug + " → " + list.size() + " catégories");
        return categoryRepository.findByMarketplace_Slug(slug)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private CategoryDto toDto(Category c) {
        return new CategoryDto(c.getId(), c.getName(), c.getMarketplace().getSlug());
    }
}