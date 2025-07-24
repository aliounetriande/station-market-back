package com.stationmarket.api.vendor.service;

import com.stationmarket.api.auth.model.User;
import com.stationmarket.api.auth.repository.UserRepository;
import com.stationmarket.api.vendor.dto.VendorDto;
import com.stationmarket.api.vendor.dto.VendorSetupDto;
import com.stationmarket.api.vendor.dto.VendorUpdateDto;
import com.stationmarket.api.vendor.model.Pack;
import com.stationmarket.api.vendor.model.Vendor;
import com.stationmarket.api.vendor.model.VendorCategory;
import com.stationmarket.api.vendor.repository.VendorRepository;
import com.stationmarket.api.vendor.service.PackService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VendorService {
    private final VendorRepository vendorRepository;
    private final UserRepository userRepository;
    private final PackService packService;    // injecte le service, pas le repo
    private final PasswordEncoder passwordEncoder;

    /**
     * Étape 2 : création du Vendor une fois l’utilisateur activé,
     * avec un pack par défaut.
     */
    @Transactional
    public void createVendor(String userEmail, VendorSetupDto dto) {
        // 1. Récupérer l’utilisateur par son email
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("Utilisateur introuvable : " + userEmail)
                );

        // 2. Récupérer le pack par défaut
        Pack defaultPack = packService.getDefaultPack();

        // 3. Construire et persister le Vendor
        Vendor vendor = Vendor.builder()
                .user(user)
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .category(dto.getCategory())
                .pack(defaultPack)
                .build();

        vendorRepository.save(vendor);
    }

    @Transactional
    public VendorDto updateVendor(String userEmail, VendorUpdateDto dto) {
        // 1) Mise à jour de l’utilisateur
        User u = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        u.setName(dto.getName());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            u.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        userRepository.save(u);

        // 2) Mise à jour du Vendor
        Vendor v = vendorRepository.findByUserId(u.getId())
                .orElseThrow(() -> new IllegalArgumentException("Vendor introuvable"));
        v.setPhone(dto.getPhone());
        v.setAddress(dto.getAddress());
        v.setCategory(dto.getCategory());
        vendorRepository.save(v);

        // 3) Reconstruire le DTO de réponse
        return VendorDto.builder()
                .id(v.getId())
                .packId(v.getPack().getId())
                .name(u.getName())
                .email(u.getEmail())
                .phone(v.getPhone())
                .address(v.getAddress())
                .category(v.getCategory().name())
                .build();
    }


    public List<Vendor> findAll() {
        return vendorRepository.findAll();
    }

    public Optional<Vendor> findById(Long id) {
        return vendorRepository.findById(id);
    }

    public Optional<Vendor> findByUserId(Long userId) {
        return vendorRepository.findByUserId(userId);
    }

    public Optional<Vendor> findByUserEmail(String email) {
        return userRepository.findByEmail(email)
                .flatMap(user -> vendorRepository.findByUserId(user.getId()));
    }

    public Vendor save(Vendor vendor) {
        return vendorRepository.save(vendor);
    }

    public void deleteById(Long id) {
        vendorRepository.deleteById(id);
    }
}
