package com.stationmarket.api.vendor.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import com.stationmarket.api.auth.model.Role;
import com.stationmarket.api.auth.model.Status;
import com.stationmarket.api.common.exception.ResourceNotFoundException;
import com.stationmarket.api.vendor.dto.InvitationDto;
import com.stationmarket.api.vendor.dto.InvitationRequestDto;
import com.stationmarket.api.vendor.dto.AcceptInvitationRequestDto;
import com.stationmarket.api.vendor.model.*;
import com.stationmarket.api.auth.model.InvitationType;
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

    @Value("${app.front-url}")
    private String frontendUrl;

    @Autowired
    public InvitationService(
            InvitationRepository invitationRepository,
            MarketplaceRepository marketplaceRepository,
            MarketplaceEditorRepository marketplaceEditorRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            VendorRepository vendorRepository,
            PackService packService,
            PasswordEncoder passwordEncoder
    ) {
        this.invitationRepository = invitationRepository;
        this.marketplaceRepository = marketplaceRepository;
        this.marketplaceEditorRepository = marketplaceEditorRepository;
        this.userRepository = userRepository;
        this.vendorRepository = vendorRepository;
        this.packService = packService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String sendInvitation(String marketplaceSlug, InvitationRequestDto request, User currentUser) {
        Marketplace marketplace = marketplaceRepository.findBySlug(marketplaceSlug)
                .orElseThrow(() -> new RuntimeException("Marketplace non trouvée"));

        if (!hasInvitePermission(currentUser, marketplace)) {
            throw new RuntimeException("Vous n'avez pas les droits pour inviter des utilisateurs");
        }

        Invitation invitation = new Invitation();
        invitation.setEmail(request.getEmail());
        invitation.setRole(request.getRole());
        invitation.setStatus("PENDING");
        invitation.setInviter(currentUser);
        invitation.setToken(UUID.randomUUID().toString());
        invitation.setMarketplace(marketplace);
        invitation.setMessage(request.getMessage());
        invitation.setCreatedAt(LocalDateTime.now());
        invitation.setExpiresAt(LocalDateTime.now().plusDays(7));

        invitationRepository.save(invitation);

        // Préparer et envoyer l'email via Resend
        String subject = "Invitation à administrer " + invitation.getMarketplace().getMarketName();
        String invitationLink = frontendUrl + "/invitation/accept?token=" + invitation.getToken();
        String htmlContent = "<p>Bonjour,<br><br>" +
                invitation.getInviter().getName() + " vous invite à administrer la page <b>" +
                invitation.getMarketplace().getMarketName() + "</b> en tant que " + invitation.getRole() + ".<br><br>" +
                (invitation.getMessage() != null ? "<b>Message :</b> " + invitation.getMessage() + "<br><br>" : "") +
                "Pour accepter cette invitation, cliquez sur le lien suivant :<br>" +
                "<a href='" + invitationLink + "'>" + invitationLink + "</a><br><br>" +
                "L'invitation expire le " + invitation.getExpiresAt().toLocalDate() + ".<br><br>" +
                "Cordialement,<br>L'équipe StationMarket</p>";

        sendResendEmail(invitation.getEmail(), subject, htmlContent);

        return "Invitation envoyée avec succès";
    }

    private boolean hasInvitePermission(User user, Marketplace marketplace) {
        // À adapter selon ta logique métier
        return true;
    }

    private void sendResendEmail(String to, String subject, String htmlContent) {
        Resend resend = new Resend("re_VPhyUsXw_9jyC8yJXPvgkpr4buXvswmjU"); // Clé API Resend
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("Station Market <contact@total-innovation.net>") // Email vérifié Resend
                .to(to)
                .subject(subject)
                .html(htmlContent)
                .build();
        try {
            CreateEmailResponse data = resend.emails().send(params);
            System.out.println("Resend email ID: " + data.getId());
        } catch (ResendException e) {
            e.printStackTrace();
        }
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

        if (!hasInvitePermission(currentUser, invitation.getMarketplace())) {
            throw new RuntimeException("Vous n'avez pas les droits pour annuler cette invitation");
        }

        invitation.setStatus("CANCELLED");
        invitationRepository.save(invitation);
    }

    public User acceptInvitation(AcceptInvitationRequestDto request) {
        Invitation invitation = invitationRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invitation invalide ou expirée"));

        if (!"PENDING".equals(invitation.getStatus())) {
            throw new RuntimeException("Cette invitation a déjà été utilisée ou annulée");
        }

        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cette invitation a expiré");
        }

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

        System.out.println("🔍 Données reçues pour Vendor:");
        System.out.println("   - Phone: " + request.getPhone());
        System.out.println("   - Address: " + request.getAddress());

        if (request.getPhone() == null || request.getPhone().isEmpty()) {
            throw new RuntimeException("Le téléphone est requis pour créer un Vendor.");
        }

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
                        .pack(packService.getDefaultPack())
                        .build();

                Vendor savedVendor = vendorRepository.save(vendor);
                System.out.println("✅ Vendor créé pour l'utilisateur : " + user.getEmail() + " avec ID : " + savedVendor.getId());
            } catch (Exception e) {
                System.err.println("❌ Erreur lors de la création du Vendor: " + e.getMessage());
                throw new RuntimeException("Erreur lors de la création du Vendor: " + e.getMessage());
            }
        }

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

        invitation.setStatus("ACCEPTED");
        invitationRepository.save(invitation);
        System.out.println("✅ Invitation marquée comme acceptée : " + invitation.getToken());

        return user;
    }

    public String validateInvitationToken(String token) {
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation non trouvée"));

        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cette invitation a expiré");
        }

        return "Token d'invitation valide";
    }

    public InvitationDto getInvitationByToken(String token) {
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation non trouvée"));

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