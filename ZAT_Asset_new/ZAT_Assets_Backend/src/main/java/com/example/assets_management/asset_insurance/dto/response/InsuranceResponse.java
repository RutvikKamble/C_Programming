package com.example.assets_management.asset_insurance.dto.response;

import com.example.assets_management.asset_insurance.enums.InsuranceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record InsuranceResponse(
        UUID id,
        UUID assetId,
        String assetCode,
        String assetName,
        String provider,
        String policyNumber,
        String coverageDetails,
        BigDecimal premium,
        LocalDate startDate,
        LocalDate expiryDate,
        InsuranceStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
