package com.stationmarket.api.vendor.dto;

import com.stationmarket.api.vendor.model.VendorCategory;
import lombok.*;

import jakarta.validation.constraints.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class VendorSetupDto {
    @NotBlank
    private String phone;

    @NotBlank
    private String address;

    @NotNull
    private VendorCategory category;

}
