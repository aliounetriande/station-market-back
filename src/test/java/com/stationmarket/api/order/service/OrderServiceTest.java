package com.stationmarket.api.order.service;

import com.stationmarket.api.order.repository.OrderRepository;
import com.stationmarket.api.withdrawal.repository.WithdrawalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private WithdrawalRepository withdrawalRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetMarketplaceBalance() {
        // Données de test
        String marketplaceSlug = "test-marketplace";
        BigDecimal totalPaidOrders = new BigDecimal("500.00");
        BigDecimal totalWithdrawn = new BigDecimal("200.00");

        // Mocks
        when(orderRepository.sumAmountByMarketplaceSlugAndStatus(marketplaceSlug, "PAID"))
                .thenReturn(totalPaidOrders);
        when(withdrawalRepository.sumAmountByMarketplaceSlugAndStatus(marketplaceSlug, "PAID"))
                .thenReturn(totalWithdrawn);

        // Appel de la méthode
        BigDecimal balance = orderService.getMarketplaceBalance(marketplaceSlug);

        // Vérifications
        assertEquals(new BigDecimal("300.00"), balance, "Le solde devrait être 300.00");

        // Vérifie que les méthodes des mocks ont été appelées
        verify(orderRepository, times(1))
                .sumAmountByMarketplaceSlugAndStatus(marketplaceSlug, "PAID");
        verify(withdrawalRepository, times(1))
                .sumAmountByMarketplaceSlugAndStatus(marketplaceSlug, "PAID");
    }

    @Test
    void testGetMarketplaceBalanceWithNullValues() {
        // Données de test
        String marketplaceSlug = "test-marketplace";

        // Mocks : Simule des valeurs nulles
        when(orderRepository.sumAmountByMarketplaceSlugAndStatus(marketplaceSlug, "PAID"))
                .thenReturn(null);
        when(withdrawalRepository.sumAmountByMarketplaceSlugAndStatus(marketplaceSlug, "PAID"))
                .thenReturn(null);

        // Appel de la méthode
        BigDecimal balance = orderService.getMarketplaceBalance(marketplaceSlug);

        // Vérifications
        assertEquals(BigDecimal.ZERO, balance, "Le solde devrait être 0.00 si aucune commande ou retrait n'existe");

        // Vérifie que les méthodes des mocks ont été appelées
        verify(orderRepository, times(1))
                .sumAmountByMarketplaceSlugAndStatus(marketplaceSlug, "PAID");
        verify(withdrawalRepository, times(1))
                .sumAmountByMarketplaceSlugAndStatus(marketplaceSlug, "PAID");
    }
}