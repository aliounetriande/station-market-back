package com.stationmarket.api.order.service;

import com.stationmarket.api.order.model.Order;
import com.stationmarket.api.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getPaidOrdersByMarketplace(String slug) {
        return orderRepository.findByMarketplaceSlugAndStatus(slug, "PAID");
    }

    public BigDecimal getMarketplaceBalance(String slug) {
        return orderRepository.sumAmountByMarketplaceSlugAndStatus(slug, "PAID");
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }
}
