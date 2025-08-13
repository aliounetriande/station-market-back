package com.stationmarket.api.auth.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "users")
@Data @NoArgsConstructor @AllArgsConstructor
@Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private Instant emailVerifiedAt;

    /** stocke le hash du mot de passe (nullable si OAuth) */
    private String password;

    private String photo;

    // ✅ AJOUTS pour le système d'invitation
    private String phone;
    private String address;

    @Enumerated(EnumType.STRING)
    private InvitationType invitedAs; // ✅ NOUVEAU

    @Builder.Default
    private Boolean profileCompleted = false; // ✅ NOUVEAU

    private String provider;     // ex. "google", "facebook"
    private String providerId;   // id renvoyé par le fournisseur

    @Enumerated(EnumType.STRING)
    private Status status;       // ACTIVE, INACTIVE

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id")
    )
    private Set<Role> roles;
}