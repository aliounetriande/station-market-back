package com.stationmarket.api.vendor.dto;

import com.stationmarket.api.vendor.model.VendorCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VendorUpdateDto {
    @NotBlank
    String name;
    String password;
    @NotBlank String phone;
    @NotBlank String address;
    @NotNull
    VendorCategory category;
}
