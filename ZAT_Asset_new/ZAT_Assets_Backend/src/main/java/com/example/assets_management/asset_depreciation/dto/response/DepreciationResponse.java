package com.example.assets_management.asset_depreciation.dto.response;

import com.example.assets_management.asset_category.enums.DepreciationMethod;
import com.example.assets_management.asset_depreciation.enums.DepreciationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record DepreciationResponse(
        UUID id,
        UUID assetId,
        String assetCode,
        String assetName,
        LocalDate periodMonth,
        BigDecimal openingValue,
        BigDecimal depreciationAmount,
        BigDecimal accumulatedDepreciation,
        BigDecimal closingValue,
        DepreciationMethod depreciationMethod,
        DepreciationStatus depreciationStatus,
        LocalDateTime createdAt
) {}
