package com.example.assets_management.asset_depreciation.dto.request;

import com.example.assets_management.asset_category.enums.DepreciationMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record DepreciationPreviewRequest(
        @NotNull @DecimalMin(value = "0.00") BigDecimal purchaseCost,
        @NotNull @DecimalMin(value = "0.00") BigDecimal salvageValue,
        @NotNull @Positive Integer usefulLifeMonths,
        @NotNull DepreciationMethod depreciationMethod,
        @NotNull @DecimalMin(value = "0.00") BigDecimal openingValue
) {}
