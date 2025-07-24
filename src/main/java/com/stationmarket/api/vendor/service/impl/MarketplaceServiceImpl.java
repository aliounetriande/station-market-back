package com.stationmarket.api.vendor.service.impl;

import com.stationmarket.api.vendor.dto.MarketplaceDto;
import com.stationmarket.api.vendor.model.Marketplace;
import com.stationmarket.api.vendor.model.Pack;
import com.stationmarket.api.vendor.model.Vendor;
import com.stationmarket.api.vendor.repository.MarketplaceRepository;
import com.stationmarket.api.vendor.service.MarketplaceService;
import com.stationmarket.api.vendor.service.PackService;
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
        m.setShortDes(dto.getShortDes());
        m.setDescription(dto.getDescription());
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

        m.setMarketName(dto.getMarketName());
        m.setShortDes(dto.getShortDes());
        m.setDescription(dto.getDescription());
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

    @Override
    public Optional<Marketplace> getMarketplaceById(Long id) {
        return marketplaceRepository.findById(id);
    }

    @Override
    public Optional<Marketplace> getMarketplaceByVendor(Vendor vendor) {
        return marketplaceRepository.findByVendor(vendor);
    }

    @Override
    public List<Marketplace> getMarketplacesByVendor(Vendor vendor) {
        return marketplaceRepository.findAllByVendor(vendor);
    }

}
