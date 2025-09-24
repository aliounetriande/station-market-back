package com.stationmarket.api.order.service;

import com.stationmarket.api.order.dto.OrderRevenusStatsDTO;
import com.stationmarket.api.order.dto.OrderStatsDTO;
import com.stationmarket.api.order.model.Order;
import com.stationmarket.api.order.repository.OrderRepository;
import com.stationmarket.api.withdrawal.repository.WithdrawalRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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
        // Si la commande est en livraison, on initialise le statut de livraison à "UNDONE"
        if ("DELIVERY".equalsIgnoreCase(order.getDeliveryMode())) {
            order.setDeliveryStatus("UNDONE");
        }
        // Tu peux aussi initialiser à null ou "" si ce n'est pas une livraison
        return orderRepository.save(order);
    }





    public List<OrderStatsDTO> getOrderStats(String slug, String period) {
        List<Order> orders = orderRepository.findByMarketplaceSlug(slug);

        Map<String, Long> stats = new LinkedHashMap<>();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH);

        for (Order order : orders) {
            LocalDate date = order.getCreatedAt().toLocalDate();
            String label;
            switch (period) {
                case "year":
                    label = String.valueOf(date.getYear());
                    break;
                case "month":
                    label = date.format(monthFormatter);
                    label = label.substring(0, 1).toUpperCase() + label.substring(1); // Majuscule
                    break;
                case "week":
                    int week = date.get(WeekFields.of(Locale.FRENCH).weekOfWeekBasedYear());
                    label = "Semaine " + week + " " + date.getYear();
                    break;
                default:
                    label = date.format(monthFormatter);
                    label = label.substring(0, 1).toUpperCase() + label.substring(1);
            }
            stats.put(label, stats.getOrDefault(label, 0L) + 1);
        }

        return stats.entrySet().stream()
                .map(e -> new OrderStatsDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }


    public List<OrderRevenusStatsDTO> getOrderRevenusStats(String slug, String period) {
        List<Order> orders = orderRepository.findByMarketplaceSlug(slug);

        Map<String, BigDecimal> stats = new LinkedHashMap<>();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH);

        for (Order order : orders) {
            LocalDate date = order.getCreatedAt().toLocalDate();
            String label;
            switch (period) {
                case "year":
                    label = String.valueOf(date.getYear());
                    break;
                case "month":
                    label = date.format(monthFormatter);
                    label = label.substring(0, 1).toUpperCase() + label.substring(1); // Majuscule
                    break;
                case "week":
                    int week = date.get(WeekFields.of(Locale.FRENCH).weekOfWeekBasedYear());
                    label = "Semaine " + week + " " + date.getYear();
                    break;
                default:
                    label = date.format(monthFormatter);
                    label = label.substring(0, 1).toUpperCase() + label.substring(1);
            }
            BigDecimal amount = order.getAmount();
            stats.put(label, stats.getOrDefault(label, BigDecimal.ZERO).add(amount));
        }

        return stats.entrySet().stream()
                .map(e -> new OrderRevenusStatsDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public List<Order> getOrdersByMarketplaceAndDeliveryStatus(String slug, String deliveryStatus) {
        return orderRepository.findByMarketplaceSlugAndDeliveryStatus(slug, deliveryStatus);
    }

    public Order getOrderByMarketplaceAndDeliveryStatusAndId(String slug, String deliveryStatus, Long id) {
        return orderRepository.findByMarketplaceSlugAndDeliveryStatusAndId(slug, deliveryStatus, id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }

    public Order getOrderByMarketplaceAndId(String slug, Long id) {
        return orderRepository.findByMarketplaceSlugAndId(slug, id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }

    public Order updateDeliveryStatus(String slug, Long id, String newStatus) {
        Order order = orderRepository.findByMarketplaceSlugAndId(slug, id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        order.setDeliveryStatus(newStatus);
        return orderRepository.save(order);
    }

    }


