package com.stationmarket.api.common.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.stationmarket.api.auth.model.Role;
import com.stationmarket.api.auth.repository.RoleRepository;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initRoles(RoleRepository roleRepo) {
        return args -> {
            for (Role.RoleName rn : Role.RoleName.values()) {
                if (roleRepo.findByName(rn).isEmpty()) {
                    Role r = new Role();
                    r.setName(rn);
                    roleRepo.save(r);
                }
            }
        };
    }
}

