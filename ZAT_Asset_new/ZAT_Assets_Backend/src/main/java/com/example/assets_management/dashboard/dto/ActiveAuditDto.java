package com.example.assets_management.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Active audit details (if an audit is currently in progress).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveAuditDto {

    private Long auditId;
    private UUID branchId;
    private String branchName;
    private LocalDate auditDate;
    private Long systemCount;
    private Long physicalCount;
    private Long scannedCount;
    private Long missingCount;
    private Double progressPercentage;
}
