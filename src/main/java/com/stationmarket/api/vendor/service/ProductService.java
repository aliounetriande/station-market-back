package com.stationmarket.api.vendor.service;

import com.stationmarket.api.vendor.dto.ProductDto;
import com.stationmarket.api.vendor.model.Category;
import com.stationmarket.api.vendor.model.Marketplace;
import com.stationmarket.api.vendor.model.Product;
import com.stationmarket.api.vendor.repository.CategoryRepository;
import com.stationmarket.api.vendor.repository.MarketplaceRepository;
import com.stationmarket.api.vendor.repository.ProductRepository;
import org.springframework.core.io.Resource;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductService {

    // dossier local uploads/
    private final Path uploadDir = Paths.get("uploads");

    @Autowired
    private ProductRepository productRepository;

    private final CategoryRepository categoryRepository;
    private final MarketplaceRepository marketplaceRepository;
    //private final ProductRepository productRepository;

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

    public ProductDto update(ProductDto dto) {
        Product product = productRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));

        Marketplace marketplace = marketplaceRepository.findBySlug(dto.getMarketplaceSlug())
                .orElseThrow(() -> new RuntimeException("Marketplace non trouvée"));

        product.setName(dto.getName());
        product.setDescription(dto.getDescription()); // Attention ici à bien appeler getDesc()
        product.setPrice(dto.getPrice());
        product.setPhoto(dto.getPhoto());
        product.setCategory(category);
        product.setMarketplace(marketplace);

        productRepository.save(product);

        // Retourne manuellement un DTO
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .photo(product.getPhoto())
                .categoryId(category.getId())
                .marketplaceSlug(marketplace.getSlug())
                .build();
    }


    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Produit non trouvé");
        }
        productRepository.deleteById(id);
    }

    public long countAllProducts() {
        return productRepository.count();
    }

    public Long countByMarketplaceSlug(String slug) {
        return productRepository.countByMarketplaceSlug(slug);
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

    /**
     * Sauvegarde le fichier et met à jour le champ photo (filename) de l’entité.
     */
//    @Transactional
//    public ProductDto storePhoto(Long id, MultipartFile file) throws IOException {
//        Product p = productRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Produit introuvable"));
//        Files.createDirectories(uploadDir);
//        String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
//        Path target = uploadDir.resolve(filename);
//        file.transferTo(target);
//        p.setPhoto(filename);
//        productRepository.save(p);
//        return ProductDto.fromEntity(p);
//    }

    @Transactional
    public ProductDto storePhoto(Long id, MultipartFile file) throws IOException {
        System.out.println("=== DEBUT STORE PHOTO ===");
        System.out.println("ID produit: " + id);
        System.out.println("Nom fichier original: " + file.getOriginalFilename());
        System.out.println("Taille fichier: " + file.getSize() + " bytes");
        System.out.println("Type MIME: " + file.getContentType());
        System.out.println("Fichier vide?: " + file.isEmpty());

        Product p = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produit introuvable"));
        System.out.println("Produit trouvé: " + p.getName());

        // Créer le dossier et afficher le chemin absolu
        Files.createDirectories(uploadDir);
        System.out.println("Dossier uploads: " + uploadDir.toAbsolutePath());

        String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();
        Path target = uploadDir.resolve(filename);
        System.out.println("Chemin complet du fichier: " + target.toAbsolutePath());

        // Sauvegarder
        file.transferTo(target);

        // Vérifier que le fichier existe
        if (Files.exists(target)) {
            System.out.println("✅ Fichier sauvegardé avec succès");
            System.out.println("Taille sur disque: " + Files.size(target) + " bytes");
        } else {
            System.err.println("❌ Fichier non trouvé après sauvegarde!");
        }

        // Mettre à jour en base
        p.setPhoto(filename);
        Product savedProduct = productRepository.save(p);
        System.out.println("✅ BDD mise à jour - filename: " + filename);

        ProductDto result = ProductDto.fromEntity(savedProduct);
        System.out.println("✅ DTO créé - photo: " + result.getPhoto());
        System.out.println("=== FIN STORE PHOTO ===");

        return result;
    }

    /**
     * Charge la ressource pour la lecture via HTTP.
     */
    public Resource loadPhotoAsResource(String filename) {
        try {
            Path file = uploadDir.resolve(filename);
            Resource res = new UrlResource(file.toUri());
            if (res.exists() || res.isReadable()) {
                return res;
            }
            throw new RuntimeException("Impossible de lire le fichier: " + filename);
        } catch (MalformedURLException e) {
            throw new RuntimeException("URL invalide pour le fichier: " + filename, e);
        }
    }

}
