package com.stationmarket.api.vendor.service;

import com.stationmarket.api.auth.model.Role;
import com.stationmarket.api.auth.model.Status;
import com.stationmarket.api.common.exception.ResourceNotFoundException;
import com.stationmarket.api.vendor.dto.InvitationDto;
import com.stationmarket.api.vendor.dto.InvitationRequestDto;
import com.stationmarket.api.vendor.dto.AcceptInvitationRequestDto;
import com.stationmarket.api.vendor.model.*;
import com.stationmarket.api.auth.model.InvitationType; // Si vous l'avez créé
import com.stationmarket.api.auth.model.User;
import com.stationmarket.api.vendor.repository.InvitationRepository;
import com.stationmarket.api.vendor.repository.MarketplaceRepository;
import com.stationmarket.api.vendor.repository.MarketplaceEditorRepository;
import com.stationmarket.api.auth.repository.UserRepository;
import com.stationmarket.api.auth.repository.RoleRepository;
import com.stationmarket.api.vendor.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;



import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final MarketplaceRepository marketplaceRepository;
    private final MarketplaceEditorRepository marketplaceEditorRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PackService packService;
    private final VendorRepository vendorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender emailSender;

    @Value("${app.front-url}")
    private String frontendUrl; // URL de l'application front-end, utilisée pour les liens d'invitation


    @Autowired
    public InvitationService(
            InvitationRepository invitationRepository,
            MarketplaceRepository marketplaceRepository,
            MarketplaceEditorRepository marketplaceEditorRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            VendorRepository vendorRepository,
            PackService packService,
            PasswordEncoder passwordEncoder,
            JavaMailSender emailSender) {
        this.invitationRepository = invitationRepository;
        this.marketplaceRepository = marketplaceRepository;
        this.marketplaceEditorRepository = marketplaceEditorRepository;
        this.userRepository = userRepository;
        this.vendorRepository = vendorRepository;
        this.packService = packService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailSender = emailSender;
    }

    public String sendInvitation(String marketplaceSlug, InvitationRequestDto request, User currentUser) {
        Marketplace marketplace = marketplaceRepository.findBySlug(marketplaceSlug)
                .orElseThrow(() -> new RuntimeException("Marketplace non trouvée"));

        // Vérifier que l'utilisateur actuel a les droits pour inviter
        if (!hasInvitePermission(currentUser, marketplace)) {
            throw new RuntimeException("Vous n'avez pas les droits pour inviter des utilisateurs");
        }

        // Créer l'invitation
        Invitation invitation = new Invitation();
        invitation.setEmail(request.getEmail());
        invitation.setRole(request.getRole());
        invitation.setStatus("PENDING");
        invitation.setInviter(currentUser);// Définir l'invitant
        invitation.setToken(UUID.randomUUID().toString());
        invitation.setMarketplace(marketplace);
        invitation.setInviter(currentUser);
        invitation.setMessage(request.getMessage());
        invitation.setCreatedAt(LocalDateTime.now());
        invitation.setExpiresAt(LocalDateTime.now().plusDays(7));

        invitationRepository.save(invitation);

        // Envoyer l'email d'invitation
        sendInvitationEmail(invitation);

        return "Invitation envoyée avec succès";
    }

    private boolean hasInvitePermission(User user, Marketplace marketplace) {
        // Logique pour vérifier si l'utilisateur peut inviter sur ce marketplace
        // Par exemple: ADMIN ou OWNER peut inviter
        return true; // À implémenter selon votre logique d'autorisations
    }


    private void sendInvitationEmail(Invitation invitation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(invitation.getEmail());
        message.setSubject("Invitation à administrer " + invitation.getMarketplace().getMarketName());

        // Utiliser l'URL configurée dans application.properties
        String invitationLink = frontendUrl + "/invitation/accept?token=" + invitation.getToken();

        message.setText("Bonjour,\n\n" +
                invitation.getInviter().getName() + " vous invite à administrer la page " +
                invitation.getMarketplace().getMarketName() + " en tant que " + invitation.getRole() + ".\n\n" +
                (invitation.getMessage() != null ? "Message: " + invitation.getMessage() + "\n\n" : "") +
                "Pour accepter cette invitation, veuillez cliquer sur le lien suivant:\n" +
                invitationLink + "\n\n" +
                "L'invitation expire le " + invitation.getExpiresAt().toLocalDate() + ".\n\n" +
                "Cordialement,\nL'équipe StationMarket");

        emailSender.send(message);
    }

    public List<InvitationDto> getPendingInvitations(String marketplaceSlug) {
        List<Invitation> invitations = invitationRepository.findByMarketplaceSlugAndStatus(marketplaceSlug, "PENDING");
        return invitations.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public void cancelInvitation(Long invitationId, User currentUser) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new RuntimeException("Invitation non trouvée"));

        // Vérifier que l'utilisateur actuel a les droits pour annuler
        if (!hasInvitePermission(currentUser, invitation.getMarketplace())) {
            throw new RuntimeException("Vous n'avez pas les droits pour annuler cette invitation");
        }

        invitation.setStatus("CANCELLED");
        invitationRepository.save(invitation);
    }

    public User acceptInvitation(AcceptInvitationRequestDto request) {
        // Étape 1 : Récupérer l'invitation
        Invitation invitation = invitationRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invitation invalide ou expirée"));

        if (!"PENDING".equals(invitation.getStatus())) {
            throw new RuntimeException("Cette invitation a déjà été utilisée ou annulée");
        }

        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cette invitation a expiré");
        }

        // Étape 2 : Vérifier si l'utilisateur existe déjà ou le créer
        User user = userRepository.findByEmail(invitation.getEmail())
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(invitation.getEmail())
                            .password(passwordEncoder.encode(request.getPassword()))
                            .name(request.getFullName())
                            .status(Status.ACTIVE)
                            .invitedAs(InvitationType.EDITOR)
                            .profileCompleted(false)
                            .build();

                    Role editorRole = roleRepository.findByName(Role.RoleName.ROLE_VENDOR)
                            .orElseThrow(() -> new RuntimeException("Rôle non trouvé"));
                    newUser.setRoles(Set.of(editorRole));

                    User savedUser = userRepository.save(newUser);
                    System.out.println("✅ Utilisateur créé : " + savedUser.getEmail());
                    return savedUser;
                });

        // Étape 3 : Créer un Vendor pour l'utilisateur
        System.out.println("🔍 Données reçues pour Vendor:");
        System.out.println("   - Phone: " + request.getPhone());
        System.out.println("   - Address: " + request.getAddress());

        if (request.getPhone() == null || request.getPhone().isEmpty()) {
            throw new RuntimeException("Le téléphone est requis pour créer un Vendor.");
        }

        // ✅ Utiliser findByUserId au lieu de findByUser
        Optional<Vendor> existingVendor = vendorRepository.findByUserId(user.getId());
        if (existingVendor.isPresent()) {
            System.out.println("⚠️ Vendor existe déjà pour l'utilisateur : " + user.getEmail());
        } else {
            try {
                Vendor vendor = Vendor.builder()
                        .user(user)
                        .phone(request.getPhone())
                        .address(request.getAddress())
                        .category(VendorCategory.Alimentation)
                        .pack(packService.getDefaultPack()) // ✅ AJOUT DU PACK PAR DÉFAUT
                        .build();

                Vendor savedVendor = vendorRepository.save(vendor);
                System.out.println("✅ Vendor créé pour l'utilisateur : " + user.getEmail() + " avec ID : " + savedVendor.getId());
            } catch (Exception e) {
                System.err.println("❌ Erreur lors de la création du Vendor: " + e.getMessage());
                throw new RuntimeException("Erreur lors de la création du Vendor: " + e.getMessage());
            }
        }

        // Étape 4 : Créer l'association MarketplaceEditor
        try {
            MarketplaceEditor editor = new MarketplaceEditor();
            editor.setUser(user);
            editor.setMarketplace(invitation.getMarketplace());
            editor.setRole("EDITOR");
            marketplaceEditorRepository.save(editor);
            System.out.println("✅ Association MarketplaceEditor créée pour l'utilisateur : " + user.getEmail());
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la création de l'association MarketplaceEditor: " + e.getMessage());
        }

        // Étape 5 : Marquer l'invitation comme acceptée
        invitation.setStatus("ACCEPTED");
        invitationRepository.save(invitation);
        System.out.println("✅ Invitation marquée comme acceptée : " + invitation.getToken());

        return user;
    }

//    public String acceptInvitation(AcceptInvitationRequestDto request) {
//        Invitation invitation = invitationRepository.findByToken(request.getToken())
//                .orElseThrow(() -> new RuntimeException("Invitation invalide ou expirée"));
//
//        if (!"PENDING".equals(invitation.getStatus())) {
//            throw new RuntimeException("Cette invitation a déjà été utilisée ou annulée");
//        }
//
//        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
//            throw new RuntimeException("Cette invitation a expiré");
//        }
//
//        // Vérifier si l'utilisateur existe déjà
//        User user = userRepository.findByEmail(invitation.getEmail())
//                .orElseGet(() -> {
//                    // Créer un nouvel utilisateur
//                    User newUser = User.builder()
//                            .email(invitation.getEmail())
//                            .password(passwordEncoder.encode(request.getPassword()))
//                            .name(request.getFullName())
//                            .status(Status.ACTIVE)
//                            .build();
//
//                    // Ajouter le rôle approprié
//                    Role editorRole = roleRepository.findByName(Role.RoleName.ROLE_VENDOR)
//                            .orElseThrow(() -> new RuntimeException("Rôle non trouvé"));
//                    newUser.setRoles(Set.of(editorRole));
//
//                    return userRepository.save(newUser);
//                });
//
//        // Créer l'association entre l'utilisateur et la marketplace
//        MarketplaceEditor editor = new MarketplaceEditor();
//        editor.setUser(user);
//        editor.setMarketplace(invitation.getMarketplace());
//        editor.setRole("EDITOR"); // Vous pouvez utiliser invitation.getRole() si vous stockez le rôle précis
//        marketplaceEditorRepository.save(editor);
//
//        // Marquer l'invitation comme acceptée
//        invitation.setStatus("ACCEPTED");
//        invitationRepository.save(invitation);
//
//        return "Invitation acceptée avec succès";
//    }

    public String validateInvitationToken(String token) {
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation non trouvée"));

        // Vérifier si l'invitation n'a pas expiré
        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette invitation a expiré");
        }

        // Ne pas changer le statut pour l'instant, on le fera lors de l'inscription réelle
        return "Token d'invitation valide";
    }

    public InvitationDto getInvitationByToken(String token) {
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation non trouvée"));

        // Vérifier si l'invitation n'a pas expiré
        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette invitation a expiré");
        }

        if (!"PENDING".equals(invitation.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette invitation a déjà été traitée");
        }

        return mapToDto(invitation);
    }

    private InvitationDto mapToDto(Invitation invitation) {
        InvitationDto dto = new InvitationDto();
        dto.setId(invitation.getId());
        dto.setEmail(invitation.getEmail());
        dto.setRole(invitation.getRole());
        dto.setStatus(invitation.getStatus());
        dto.setToken(invitation.getToken());
        dto.setMarketplaceId(invitation.getMarketplace().getId());
        dto.setMarketplaceSlug(invitation.getMarketplace().getSlug());
        dto.setInviterName(invitation.getInviter().getName());
        dto.setMessage(invitation.getMessage());
        dto.setCreatedAt(invitation.getCreatedAt());
        dto.setExpiresAt(invitation.getExpiresAt());
        return dto;
    }
}
