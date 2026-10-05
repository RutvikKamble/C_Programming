package com.example.assets_management.asset_disposal.dto.response;

import com.example.assets_management.asset_disposal.enums.DisposalStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/** API response for an asset disposal record. */
public record DisposalResponse(
        UUID id,
        UUID assetId,
        String assetCode,
        String assetName,
        DisposalStatus status,
        String reason,
        UUID reviewedById,
        String reviewedByName,
        UUID approvedById,
        String approvedByName,
        UUID finalizedById,
        String finalizedByName,
        LocalDate disposalDate,
        BigDecimal disposalValue,
        String disposalMethod,
        String rejectionReason,
        String remarks,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
