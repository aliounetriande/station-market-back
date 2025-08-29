package com.stationmarket.api.withdrawal.dto;

import java.math.BigDecimal;

public class WithdrawalRequestDto {
    private String vendorEmail;
    private String marketplaceSlug;
    private BigDecimal amount;
}
