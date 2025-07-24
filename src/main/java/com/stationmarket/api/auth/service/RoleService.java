package com.stationmarket.api.auth.service;

import com.stationmarket.api.auth.model.Role;
import com.stationmarket.api.auth.model.Role.RoleName;
import com.stationmarket.api.auth.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service de gestion des rôles.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;

    /**
     * Récupère tous les rôles.
     */
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    /**
     * Recherche un rôle par son nom d'ENUM.
     */
    public Role getByName(RoleName name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new IllegalArgumentException("Rôle non trouvé : " + name));
    }

    /**
     * Enregistre ou met à jour un rôle.
     */
    public Role save(Role role) {
        return roleRepository.save(role);
    }

    /**
     * Supprime un rôle par son identifiant.
     */
    public void deleteById(Long id) {
        roleRepository.deleteById(id);
    }

    /**
     * Initialise en base tous les rôles de l'ENUM (utile pour le seed).
     */
    public void initDefaultRoles() {
        for (RoleName rn : RoleName.values()) {
            if (roleRepository.findByName(rn).isEmpty()) {
                Role r = new Role();
                r.setName(rn);
                roleRepository.save(r);
            }
        }
    }
}
