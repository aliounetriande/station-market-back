package com.stationmarket.api.withdrawal.service;

import com.stationmarket.api.withdrawal.model.Withdrawal;
import com.stationmarket.api.withdrawal.repository.WithdrawalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WithdrawalService {
    @Autowired
    private WithdrawalRepository withdrawalRepository;

    public Withdrawal requestWithdrawal(Withdrawal withdrawal) {
        withdrawal.setStatus("PENDING");
        withdrawal.setRequestedAt(LocalDateTime.now());
        return withdrawalRepository.save(withdrawal);
    }

    public List<Withdrawal> getWithdrawalsByVendor(String vendorEmail) {
        return withdrawalRepository.findByVendorEmail(vendorEmail);
    }

    public List<Withdrawal> getWithdrawalsByMarketplace(String marketplaceSlug) {
        return withdrawalRepository.findByMarketplaceSlug(marketplaceSlug);
    }

    public Withdrawal updateWithdrawalStatus(Long id, String status) {
        Withdrawal withdrawal = withdrawalRepository.findById(id).orElseThrow();
        withdrawal.setStatus(status);
        withdrawal.setProcessedAt(LocalDateTime.now());
        return withdrawalRepository.save(withdrawal);
    }

    public List<Withdrawal> getWithdrawalsByStatus(String status) {
        return withdrawalRepository.findByStatus(status);
    }

    public long countByStatus(String status) {
        return withdrawalRepository.countByStatus(status);
    }
}
