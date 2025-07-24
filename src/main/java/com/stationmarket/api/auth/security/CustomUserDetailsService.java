package com.stationmarket.api.auth.security;

import com.stationmarket.api.auth.model.Status;
import com.stationmarket.api.auth.repository.UserRepository;
import com.stationmarket.api.auth.model.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Rechercher l'utilisateur par email
        User u = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // Charger les rôles de l'utilisateur et les convertir en SimpleGrantedAuthority
        var authorities = u.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))  // Exemple d'accès à l'énumération du rôle
                .collect(Collectors.toList());

        // Retourner un CustomUserDetails avec les autorités (rôles)
        return new CustomUserDetails(u.getId(), u.getEmail(), u.getPassword(), authorities, u.getStatus() == Status.ACTIVE);
    }
}
