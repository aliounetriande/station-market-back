package com.stationmarket.api.order.controller;

//import com.stationmarket.api.order.dto.OrderDto;
import com.stationmarket.api.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    @WithMockUser(authorities = "ROLE_VENDOR")
    void testGetBalance() throws Exception {
        // Données de test
        String marketplaceSlug = "test-marketplace";
        BigDecimal balance = new BigDecimal("500.00");

        // Mock du service
        when(orderService.getMarketplaceBalance(marketplaceSlug)).thenReturn(balance);

        // Appel de l'endpoint
        mockMvc.perform(get("/api/marketplaces/{slug}/balance", marketplaceSlug)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("500.00"));

        // Vérification que le service a été appelé une fois
        verify(orderService, times(1)).getMarketplaceBalance(marketplaceSlug);
    }

}