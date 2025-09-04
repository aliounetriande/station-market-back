package com.stationmarket.api.withdrawal.controller;

import com.stationmarket.api.withdrawal.model.Withdrawal;
import com.stationmarket.api.withdrawal.service.WithdrawalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/withdrawals")
public class WithdrawalController {
    @Autowired
    private WithdrawalService withdrawalService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<Withdrawal> getAllWithdrawals() {
        return withdrawalService.getAllWithdrawals();
    }

    @PostMapping
    public Withdrawal requestWithdrawal(@RequestBody Withdrawal withdrawal) {
        return withdrawalService.requestWithdrawal(withdrawal);
    }

    @GetMapping("/vendor/{vendorEmail}")
    public List<Withdrawal> getByVendor(@PathVariable String vendorEmail) {
        return withdrawalService.getWithdrawalsByVendor(vendorEmail);
    }

    @GetMapping("/marketplace/{marketplaceSlug}")
    public List<Withdrawal> getByMarketplace(@PathVariable String marketplaceSlug) {
        return withdrawalService.getWithdrawalsByMarketplace(marketplaceSlug);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Withdrawal updateStatus(@PathVariable Long id, @RequestParam String status) {
        return withdrawalService.updateWithdrawalStatus(id, status);
    }

    // Dans WithdrawalController
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<Withdrawal> getByStatus(@PathVariable String status) {
        return withdrawalService.getWithdrawalsByStatus(status);
    }

    @GetMapping("/count/by-status/{status}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public long countByStatus(@PathVariable String status) {
        return withdrawalService.countByStatus(status);
    }

}
