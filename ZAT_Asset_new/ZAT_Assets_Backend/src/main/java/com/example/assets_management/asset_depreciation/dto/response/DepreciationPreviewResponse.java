package com.example.assets_management.asset_depreciation.dto.response;

import com.example.assets_management.asset_category.enums.DepreciationMethod;

import java.math.BigDecimal;

public record DepreciationPreviewResponse(
        DepreciationMethod depreciationMethod,
        BigDecimal monthlyDepreciation,
        BigDecimal closingValue
) {}
