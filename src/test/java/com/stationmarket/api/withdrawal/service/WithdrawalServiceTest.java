package com.stationmarket.api.withdrawal.service;

import com.stationmarket.api.order.service.OrderService;
import com.stationmarket.api.withdrawal.model.Withdrawal;
import com.stationmarket.api.withdrawal.repository.WithdrawalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WithdrawalServiceTest {

    @Mock
    private WithdrawalRepository withdrawalRepository;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private WithdrawalService withdrawalService;

    @Test
    void testRequestWithdrawal_Success() {
        // Données de test
        String marketplaceSlug = "test-marketplace";
        BigDecimal balance = new BigDecimal("500.00");
        BigDecimal requestedAmount = new BigDecimal("300.00");

        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setMarketplaceSlug(marketplaceSlug);
        withdrawal.setAmount(requestedAmount);

        // Mocks
        when(orderService.getMarketplaceBalance(marketplaceSlug)).thenReturn(balance);
        when(withdrawalRepository.save(any(Withdrawal.class))).thenAnswer(invocation -> {
            Withdrawal savedWithdrawal = invocation.getArgument(0);
            savedWithdrawal.setId(1L);
            savedWithdrawal.setRequestedAt(LocalDateTime.now());
            return savedWithdrawal;
        });

        // Appel de la méthode
        Withdrawal result = withdrawalService.requestWithdrawal(withdrawal);

        // Vérifications
        assertNotNull(result);
        assertEquals("PENDING", result.getStatus());
        assertEquals(0, requestedAmount.compareTo(result.getAmount()));
        assertEquals(marketplaceSlug, result.getMarketplaceSlug());
        assertNotNull(result.getRequestedAt());

        // Vérifie que les méthodes des mocks ont été appelées
        verify(orderService, times(1)).getMarketplaceBalance(marketplaceSlug);
        verify(withdrawalRepository, times(1)).save(any(Withdrawal.class));
    }

    @Test
    void testRequestWithdrawal_InsufficientBalance() {
        // Données de test
        String marketplaceSlug = "test-marketplace";
        BigDecimal balance = new BigDecimal("200.00");
        BigDecimal requestedAmount = new BigDecimal("300.00");

        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setMarketplaceSlug(marketplaceSlug);
        withdrawal.setAmount(requestedAmount);

        // Mocks
        when(orderService.getMarketplaceBalance(marketplaceSlug)).thenReturn(balance);

        // Appel de la méthode et vérification de l'exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            withdrawalService.requestWithdrawal(withdrawal);
        });

        assertEquals("Le montant demandé dépasse le solde disponible.", exception.getMessage());

        // Vérifie que les méthodes des mocks ont été appelées
        verify(orderService, times(1)).getMarketplaceBalance(marketplaceSlug);
        verify(withdrawalRepository, never()).save(any(Withdrawal.class));
    }





}