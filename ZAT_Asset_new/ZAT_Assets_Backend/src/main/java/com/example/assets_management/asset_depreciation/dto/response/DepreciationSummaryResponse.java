package com.example.assets_management.asset_depreciation.dto.response;

import com.example.assets_management.asset_category.enums.DepreciationMethod;
import com.example.assets_management.asset_depreciation.enums.DepreciationStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record DepreciationSummaryResponse(
        UUID assetId,
        String assetCode,
        BigDecimal purchaseCost,
        BigDecimal salvageValue,
        BigDecimal currentValue,
        BigDecimal accumulatedDepreciation,
        DepreciationMethod depreciationMethod,
        Integer usefulLifeMonths,
        Integer elapsedLifeMonths,
        Integer remainingLifeMonths,
        DepreciationStatus depreciationStatus
) {}
