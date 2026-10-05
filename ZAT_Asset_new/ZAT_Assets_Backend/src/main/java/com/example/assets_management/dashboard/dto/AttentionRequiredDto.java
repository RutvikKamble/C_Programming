package com.example.assets_management.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Items requiring Admin attention.
 * These are actionable numbers, not duplicate total KPIs.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttentionRequiredDto {

    private Long maintenanceDue;
    private Long overdueMaintenance;
    private Long warrantyExpiring;
    private Long pendingRequests;
    private Long auditDiscrepancies;
    private Long damagedAssets;
}
