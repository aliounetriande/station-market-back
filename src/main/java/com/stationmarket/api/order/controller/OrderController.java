package com.stationmarket.api.order.controller;

import com.stationmarket.api.order.dto.OrderRevenusStatsDTO;
import com.stationmarket.api.order.dto.OrderStatsDTO;
import com.stationmarket.api.order.model.Order;
import com.stationmarket.api.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/marketplaces")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/{slug}/orders")
    @PreAuthorize("hasAuthority('ROLE_VENDOR')")
    public List<Order> getOrders(@PathVariable String slug) {
        return orderService.getPaidOrdersByMarketplace(slug);
    }

    @GetMapping("/{slug}/balance")
    @PreAuthorize("hasAuthority('ROLE_VENDOR')")
    public BigDecimal getBalance(@PathVariable String slug) {
        return orderService.getMarketplaceBalance(slug);
    }

    @GetMapping("/{slug}/orders/count")
    @PreAuthorize("hasAuthority('ROLE_VENDOR')")
    public long countOrders(@PathVariable String slug) {
        return orderService.countOrdersByMarketplace(slug);
    }

    @GetMapping("/{slug}/orders/stats")
    @PreAuthorize("hasAuthority('ROLE_VENDOR')")
    public List<OrderStatsDTO> getOrderStats(
            @PathVariable String slug,
            @RequestParam String period // "year", "month", "week"
    ) {
        return orderService.getOrderStats(slug, period);
    }

    @GetMapping("/{slug}/orders/revenus-stats")
    @PreAuthorize("hasAuthority('ROLE_VENDOR')")
    public List<OrderRevenusStatsDTO> getOrderRevenusStats(
            @PathVariable String slug,
            @RequestParam String period // "year", "month", "week"
    ) {
        return orderService.getOrderRevenusStats(slug, period);
    }
}