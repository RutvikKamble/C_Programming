package com.example.assets_management.asset_depreciation.engine;

import java.math.BigDecimal;

public interface DepreciationCalculator {
    BigDecimal calculate(BigDecimal purchaseCost,
                         BigDecimal salvageValue,
                         int usefulLifeMonths,
                         BigDecimal openingValue);
}
