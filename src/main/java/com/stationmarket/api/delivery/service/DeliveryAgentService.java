package com.stationmarket.api.delivery.service;

import com.stationmarket.api.auth.model.Role;
import com.stationmarket.api.auth.model.User;
import com.stationmarket.api.auth.model.Status;
import com.stationmarket.api.auth.repository.RoleRepository;
import com.stationmarket.api.auth.repository.UserRepository;
import com.stationmarket.api.delivery.dto.UpdateDeliveryAgentDto;
import com.stationmarket.api.delivery.model.DeliveryAgent;
import com.stationmarket.api.delivery.repository.DeliveryAgentRepository;
import com.stationmarket.api.vendor.model.Marketplace;
import com.stationmarket.api.vendor.repository.MarketplaceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class DeliveryAgentService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MarketplaceRepository marketplaceRepository;
    private final DeliveryAgentRepository deliveryAgentRepository;
    private final PasswordEncoder passwordEncoder;

    public DeliveryAgent createDeliveryAgent(String name, String email, String phone, String address, String password, Long marketplaceId) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email déjà utilisé");
        }
        Role livreurRole = roleRepository.findByName(Role.RoleName.ROLE_DELIVERY)
                .orElseThrow(() -> new IllegalArgumentException("Role LIVREUR non trouvé"));

        User user = User.builder()
                .name(name)
                .email(email)
                .phone(phone)
                .address(address)
                .password(passwordEncoder.encode(password))
                .roles(Set.of(livreurRole))
                .status(Status.ACTIVE)
                .build();
        userRepository.save(user);

        Marketplace marketplace = marketplaceRepository.findById(marketplaceId)
                .orElseThrow(() -> new IllegalArgumentException("Marketplace introuvable"));

        DeliveryAgent agent = DeliveryAgent.builder()
                .user(user)
                .marketplace(marketplace)
                .build();
        return deliveryAgentRepository.save(agent);
    }

    @Transactional
    public DeliveryAgent updateDeliveryAgent(Long agentId, UpdateDeliveryAgentDto dto) {
        DeliveryAgent agent = deliveryAgentRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("Livreur introuvable"));

        User user = agent.getUser();
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getAddress() != null) user.setAddress(dto.getAddress());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        userRepository.save(user);
        return agent;
    }

    public DeliveryAgent getById(Long agentId) {
        return deliveryAgentRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("Livreur introuvable"));
    }

    public java.util.List<DeliveryAgent> getByMarketplace(Long marketplaceId) {
        return deliveryAgentRepository.findByMarketplaceId(marketplaceId);
    }

    @Transactional
    public void deleteDeliveryAgent(Long agentId) {
        DeliveryAgent agent = deliveryAgentRepository.findById(agentId).orElse(null);
        if (agent != null) {
            // Supprime d'abord le DeliveryAgent (pour éviter les contraintes de clé étrangère)
            deliveryAgentRepository.delete(agent);
            // Puis supprime le User associé
            userRepository.delete(agent.getUser());
        }
    }
}
