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
import java.util.Map;

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

    @GetMapping("/orders/delivery-status")
    @PreAuthorize("hasAuthority('ROLE_DELIVERY')")
    public List<Order> getOrdersByDeliveryStatus(
            @RequestParam String marketplaceSlug,
            @RequestParam String status
    ) {
        return orderService.getOrdersByMarketplaceAndDeliveryStatus(marketplaceSlug, status);
    }

    @GetMapping("/orders/delivery-status/{id}")
    @PreAuthorize("hasAuthority('ROLE_DELIVERY')")
    public Order getOrderByDeliveryStatusAndMarketplace(
            @RequestParam String marketplaceSlug,
            @RequestParam String status,
            @PathVariable Long id
    ) {
        return orderService.getOrderByMarketplaceAndDeliveryStatusAndId(marketplaceSlug, status, id);
    }

    @PatchMapping("/{slug}/orders/{id}/delivery-status")
    @PreAuthorize("hasAuthority('ROLE_DELIVERY')")
    public Order updateDeliveryStatus(
            @PathVariable String slug,
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        String newStatus = body.get("deliveryStatus");
        return orderService.updateDeliveryStatus(slug, id, newStatus);
    }

    @GetMapping("/{slug}/orders/{id}")
    @PreAuthorize("hasAuthority('ROLE_DELIVERY')")
    public Order getOrderByMarketplaceAndId(
            @PathVariable String slug,
            @PathVariable Long id
    ) {
        return orderService.getOrderByMarketplaceAndId(slug, id);
    }
}