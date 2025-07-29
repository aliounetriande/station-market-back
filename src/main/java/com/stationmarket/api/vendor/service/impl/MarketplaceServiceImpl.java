package com.stationmarket.api.vendor.service.impl;

import com.stationmarket.api.vendor.dto.MarketplaceDto;
import com.stationmarket.api.vendor.dto.MarketplaceListDto;
import com.stationmarket.api.vendor.model.Marketplace;
import com.stationmarket.api.vendor.model.Pack;
import com.stationmarket.api.vendor.model.Vendor;
import com.stationmarket.api.vendor.repository.MarketplaceRepository;
import com.stationmarket.api.vendor.service.MarketplaceService;
import com.stationmarket.api.vendor.service.PackService;
import com.stationmarket.api.vendor.service.VendorService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MarketplaceServiceImpl implements MarketplaceService {

    private final MarketplaceRepository marketplaceRepository;
    private final PackService packService;
    private final VendorService vendorService;

    @Override
    public Marketplace createMarketplace(MarketplaceDto dto, Vendor vendor) {
        if (vendor == null) {
            throw new IllegalArgumentException("Le vendeur doit être connecté.");
        }

        // Vérifiez si le vendeur a atteint la limite de Marketplaces selon son pack
        Pack vendorPack = vendor.getPack();
        long currentMarketplaceCount = marketplaceRepository.countByVendor(vendor);

        if (currentMarketplaceCount >= vendorPack.getMaxMarketplaces()) {
            throw new IllegalStateException("Le nombre de Marketplaces autorisées a été atteint pour ce pack.");
        }


        // Création de nouvelle Marketplace
        Marketplace m = new Marketplace();
        m.setMarketName(dto.getMarketName());
        m.setEmail(dto.getEmail());
        m.setPhone(dto.getPhone());
        m.setAddress(dto.getAddress());
        m.setLogo(dto.getLogo());
        m.setThemeColor(dto.getThemeColor());
        m.setSocialLinks(dto.getSocialLinks());
        m.setOpenHours(dto.getOpenHours());
        String base = toSlug(dto.getSlug());
        m.setSlug(makeUnique(base));
        m.setVendor(vendor);
        Marketplace savedMarketplace = marketplaceRepository.save(m);

        // Vérifiez que la marketplace a bien été sauvegardée
        if (savedMarketplace != null) {
            return savedMarketplace;
        } else {
            throw new RuntimeException("Erreur lors de la création de la marketplace");
        }
    }

    @Override
    public Marketplace updateMarketplace(Long id, MarketplaceDto dto, Vendor vendor) {
        Marketplace m = marketplaceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Marketplace non trouvé"));

        if (!m.getVendor().getId().equals(vendor.getId())) {
            throw new SecurityException("Vous n’avez pas accès à cette marketplace");
        }
        // si slug changé, régénérez‑le et vérifiez l’unicité
        if (!m.getSlug().equals(dto.getSlug())) {
            String candidate = toSlug(dto.getSlug());
            m.setSlug(makeUnique(candidate));
        }

        m.setMarketName(dto.getMarketName());
        m.setShortDes(dto.getShortDes());
        m.setEmail(dto.getEmail());
        m.setPhone(dto.getPhone());
        m.setAddress(dto.getAddress());
        m.setLogo(dto.getLogo());
        m.setPhoto(dto.getPhoto());
        m.setStatus(dto.getStatus());
        m.setThemeColor(dto.getThemeColor());
        m.setSocialLinks(dto.getSocialLinks());
        m.setOpenHours(dto.getOpenHours());
        m.setMaintenanceMode(dto.getMaintenanceMode());

        return marketplaceRepository.save(m);
    }

    private String toSlug(String input) {
        return input.trim().toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }

    private String makeUnique(String base) {
        String slug = base;
        int i = 0;
        while (marketplaceRepository.existsBySlug(slug)) {
            slug = base + "-" + (++i);
        }
        return slug;
    }

    // nouvel endpoint pour récupérer par slug
    @Override
    public Optional<Marketplace> findBySlug(String slug) {
        return marketplaceRepository.findBySlug(slug);
    }

    @Override
    public Optional<Marketplace> getMarketplaceById(Long id) {
        return marketplaceRepository.findById(id);
    }

    @Override
    public Optional<Marketplace> getMarketplaceByVendor(Vendor vendor) {
        return marketplaceRepository.findByVendor(vendor);
    }

    //@Override
   // public List<Marketplace> getMarketplacesByVendor(Vendor vendor) {
       // return marketplaceRepository.findAllByVendor(vendor);
    //}

    @Override
    public List<MarketplaceListDto> listMyMarketplaces(String userEmail) {
        // 1) récupérer le Vendor associé à cet utilisateur
        Long vendorId = vendorService.getVendorIdByEmail(userEmail);

        // 2) déléguer au repository qui exécute votre JPQL projection
        return marketplaceRepository.findAllDtoByVendorId(vendorId);
    }
}
