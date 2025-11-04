package com.stationmarket.api.vendor.controller;

import com.stationmarket.api.vendor.dto.ProductDto;
import com.stationmarket.api.vendor.service.ProductService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService ps) {
        this.productService = ps;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_VENDOR')")
    public ProductDto create(@RequestBody ProductDto dto) {
        return productService.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_VENDOR')")
    public ProductDto update(@PathVariable Long id, @RequestBody ProductDto dto) {
        dto.setId(id);
        return productService.update(dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_VENDOR')")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }

    @GetMapping("/count")
    @PreAuthorize("hasAuthority('ROLE_VENDOR')")
    public ResponseEntity<Long> countProducts() {
        return ResponseEntity.ok(productService.countAllProducts());
    }

    @GetMapping("/count/by-marketplace/{slug}")
    @PreAuthorize("hasAuthority('ROLE_VENDOR')")
    public ResponseEntity<Long> countProductsByMarketplace(@PathVariable String slug) {
        return ResponseEntity.ok(productService.countByMarketplaceSlug(slug));
    }


    @GetMapping("/by-marketplace/{slug}")
    public ResponseEntity<List<ProductDto>> listByMarketplace(@PathVariable String slug) {
        List<ProductDto> list = productService.listByMarketplaceSlug(slug);
        return ResponseEntity.ok(list);
    }

    // ─────────────── Upload / Lecture de la photo ───────────────

    /**
     * Upload d'une photo pour un produit donné.
     * Le fichier est sauvegardé sur disque et le nom enregistré en bdd.
     */
    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ROLE_VENDOR')")
    public ResponseEntity<ProductDto> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {

        ProductDto updated = productService.storePhoto(id, file);
        return ResponseEntity.ok(updated);
    }

    /**
     * Sert la photo stockée. L’URL d’accès sera /api/products/photo/{filename}
     */
    @GetMapping("/photo/{filename:.+}")
    //@CrossOrigin(origins = "*")
    public ResponseEntity<Resource> servePhoto(@PathVariable String filename) {
        try {
            System.out.println("=== DEBUG: Tentative de servir le fichier: " + filename);

            Path file = Paths.get("uploads").resolve(filename);
            System.out.println("=== DEBUG: Chemin complet: " + file.toAbsolutePath());
            System.out.println("=== DEBUG: Dossier de travail: " + System.getProperty("user.dir"));

            Resource resource = new UrlResource(file.toUri());

            System.out.println("=== DEBUG: Fichier existe: " + resource.exists());
            System.out.println("=== DEBUG: Fichier lisible: " + resource.isReadable());

            if (!resource.exists() || !resource.isReadable()) {
                System.err.println("=== ERREUR: Impossible de lire le fichier: " + filename);
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(file);
            System.out.println("=== DEBUG: Type de contenu: " + contentType);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType != null ? contentType : "application/octet-stream")
                   // .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                    .body(resource);
        } catch (Exception e) {
            System.err.println("=== ERREUR EXCEPTION: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
