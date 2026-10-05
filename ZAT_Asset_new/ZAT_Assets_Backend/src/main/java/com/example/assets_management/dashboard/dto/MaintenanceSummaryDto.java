package com.example.assets_management.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Maintenance summary statistics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceSummaryDto {

    private Long totalWorkOrders;
    private Long openWorkOrders;
    private Long created;
    private Long assigned;
    private Long inProgress;
    private Long completed;
    private Long closed;
    private BigDecimal totalMaintenanceCost;
}
