package com.stationmarket.api.order.service;

import com.stationmarket.api.order.dto.OrderRevenusStatsDTO;
import com.stationmarket.api.order.dto.OrderStatsDTO;
import com.stationmarket.api.order.model.Order;
import com.stationmarket.api.order.repository.OrderRepository;
import com.stationmarket.api.withdrawal.repository.WithdrawalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public List<Order> getPaidOrdersByMarketplace(String slug) {
        return orderRepository.findByMarketplaceSlugAndStatus(slug, "PAID");
    }

    @Autowired
    private WithdrawalRepository withdrawalRepository;

    public BigDecimal getMarketplaceBalance(String slug) {
        BigDecimal totalPaidOrders = orderRepository.sumAmountByMarketplaceSlugAndStatus(slug, "PAID");
        if (totalPaidOrders == null) totalPaidOrders = BigDecimal.ZERO;

        BigDecimal totalWithdrawn = withdrawalRepository.sumAmountByMarketplaceSlugAndStatus(slug, "PAID");
        if (totalWithdrawn == null) totalWithdrawn = BigDecimal.ZERO;

        return totalPaidOrders.subtract(totalWithdrawn);
    }

    public Long countOrdersByMarketplace(String slug) {
        return orderRepository.countByMarketplaceSlug(slug);
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }





        public List<OrderStatsDTO> getOrderStats(String slug, String period) {
            List<Order> orders = orderRepository.findByMarketplaceSlug(slug);

            Map<String, Long> stats = new LinkedHashMap<>();
            DateTimeFormatter formatter;

            switch (period) {
                case "year":
                    formatter = DateTimeFormatter.ofPattern("yyyy");
                    break;
                case "month":
                    formatter = DateTimeFormatter.ofPattern("yyyy-MM");
                    break;
                case "week":
                    formatter = DateTimeFormatter.ofPattern("YYYY-'W'ww"); // ISO week
                    break;
                default:
                    formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            }

            for (Order order : orders) {
                LocalDate date = order.getCreatedAt().toLocalDate();
                String label = date.format(formatter);
                stats.put(label, stats.getOrDefault(label, 0L) + 1);
            }

            return stats.entrySet().stream()
                    .map(e -> new OrderStatsDTO(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());
        }


    public List<OrderRevenusStatsDTO> getOrderRevenusStats(String slug, String period) {
        List<Order> orders = orderRepository.findByMarketplaceSlug(slug);

        Map<String, BigDecimal> stats = new LinkedHashMap<>();
        DateTimeFormatter formatter;
        switch (period) {
            case "year": formatter = DateTimeFormatter.ofPattern("yyyy"); break;
            case "month": formatter = DateTimeFormatter.ofPattern("yyyy-MM"); break;
            case "week": formatter = DateTimeFormatter.ofPattern("YYYY-'W'ww"); break;
            default: formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        }

        for (Order order : orders) {
            LocalDate date = order.getCreatedAt().toLocalDate();
            String label = date.format(formatter);
            BigDecimal amount = order.getAmount(); // ou getAmount()
            stats.put(label, stats.getOrDefault(label, BigDecimal.ZERO).add(amount));
        }

        return stats.entrySet().stream()
                .map(e -> new OrderRevenusStatsDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    }


