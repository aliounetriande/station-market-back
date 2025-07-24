package com.stationmarket.api.auth.security;

import com.stationmarket.api.auth.model.Status;
import com.stationmarket.api.auth.model.User;
import com.stationmarket.api.auth.model.Role;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import lombok.*;
import java.util.*;

@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    private Long id;
    private String username;       // ici on utilisera l’email
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean enabled;

    public static CustomUserDetails fromUserEntity(User user) {
        List<SimpleGrantedAuthority> auths = user.getRoles().stream()
                .map(Role::getName)
                .map(Enum::name)
                .map(SimpleGrantedAuthority::new)
                .toList();
        return new CustomUserDetails(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                auths,
                user.getStatus() == Status.ACTIVE
        );
    }

    @Override public boolean isAccountNonExpired()     { return enabled; }
    @Override public boolean isAccountNonLocked()      { return enabled; }
    @Override public boolean isCredentialsNonExpired() { return enabled; }
    @Override public boolean isEnabled()               { return enabled; }
}

