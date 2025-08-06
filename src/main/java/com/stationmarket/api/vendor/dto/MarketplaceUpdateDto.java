package com.stationmarket.api.vendor.dto;

import com.stationmarket.api.vendor.model.MarketplaceStatus;
import lombok.*;

import jakarta.validation.constraints.*;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketplaceUpdateDto {
    @NotBlank(message = "Le nom de la marketplace ne peut pas être vide.")
    private String marketName;


    private String shortDes;

    @NotBlank(message = "Le logo ne peut pas être vide.")
    private String logo;

    @NotBlank(message = "L’adresse ne peut pas être vide.")
    private String address;


    private String photo;

    @Email(message = "L’email doit être valide.")
    @NotBlank(message = "L’email ne peut pas être vide.")
    private String email;

    @NotBlank(message = "Le téléphone ne peut pas être vide.")
    private String phone;

    @NotBlank
    private String slug;

    @NotBlank(message = "La couleur thème ne peut pas être vide.")
    private String themeColor;


    private Map<@NotBlank String, @NotBlank String> socialLinks;

    private String openHours;

    @NotNull(message = "Le statut doit être défini.")
    private MarketplaceStatus status;

    @NotNull
    private Boolean maintenanceMode;
}
