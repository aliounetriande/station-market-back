package com.stationmarket.api.order.dto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatsDTO {
    private String label; // ex: "2025-09" ou "Semaine 36"
    private long count;
    // getters/setters
}
