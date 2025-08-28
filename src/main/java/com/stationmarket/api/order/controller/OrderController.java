package com.stationmarket.api.order.controller;

import com.stationmarket.api.order.model.Order;
import com.stationmarket.api.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}