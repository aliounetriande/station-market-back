package com.stationmarket.api.vendor.service;

import com.stationmarket.api.vendor.model.Pack;
import com.stationmarket.api.vendor.model.PackType;
import com.stationmarket.api.vendor.repository.PackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PackService {
    private final PackRepository packRepository;

    // Récupérer le pack de base (BASIC) par défaut
    public Pack getDefaultPack() {
        return packRepository.findByPackType(PackType.BASIC)
                .orElseThrow(() -> new IllegalStateException("Le pack de base n'est pas configuré."));
    }

    // Récupérer un pack par type
    public Pack getPackByType(PackType packType) {
        return packRepository.findByPackType(packType)
                .orElseThrow(() -> new IllegalStateException("Le pack " + packType + " n'est pas disponible."));
    }
}

