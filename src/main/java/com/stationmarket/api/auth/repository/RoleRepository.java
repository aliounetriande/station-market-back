package com.stationmarket.api.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.stationmarket.api.auth.model.Role;
import com.stationmarket.api.auth.model.Role.RoleName;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}