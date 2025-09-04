package com.stationmarket.api.withdrawal.service;

import com.stationmarket.api.order.repository.OrderRepository;
import com.stationmarket.api.order.service.OrderService;
import com.stationmarket.api.withdrawal.model.Withdrawal;
import com.stationmarket.api.withdrawal.repository.WithdrawalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WithdrawalService {
    @Autowired
    private WithdrawalRepository withdrawalRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    public Withdrawal requestWithdrawal(Withdrawal withdrawal) {
        BigDecimal balance = orderService.getMarketplaceBalance(withdrawal.getMarketplaceSlug());
        BigDecimal requestedAmount = withdrawal.getAmount();

        if (requestedAmount.compareTo(balance) > 0) {
            throw new RuntimeException("Le montant demandé dépasse le solde disponible.");
        }
        withdrawal.setStatus("PENDING");
        withdrawal.setRequestedAt(LocalDateTime.now());
        return withdrawalRepository.save(withdrawal);
    }

    public List<Withdrawal> getAllWithdrawals() {
        return withdrawalRepository.findAll();
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

    public BigDecimal getMarketplaceBalance(String marketplaceSlug) {
        BigDecimal totalPaidOrders = orderRepository.sumAmountByMarketplaceSlugAndStatus(marketplaceSlug, "PAID");
        if (totalPaidOrders == null) totalPaidOrders = BigDecimal.ZERO;

        BigDecimal totalWithdrawn = withdrawalRepository.sumAmountByMarketplaceSlugAndStatus(marketplaceSlug, "PAID");
        if (totalWithdrawn == null) totalWithdrawn = BigDecimal.ZERO;

        return totalPaidOrders.subtract(totalWithdrawn);
    }

    public long countByStatus(String status) {
        return withdrawalRepository.countByStatus(status);
    }
}
