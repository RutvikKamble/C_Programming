package com.example.assets_management.asset_depreciation.engine;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class StraightLineCalculator implements DepreciationCalculator {
    @Override
    public BigDecimal calculate(BigDecimal purchaseCost, BigDecimal salvageValue,
                                int usefulLifeMonths, BigDecimal openingValue) {
        BigDecimal monthly = purchaseCost.subtract(salvageValue)
                .divide(BigDecimal.valueOf(usefulLifeMonths), 10, RoundingMode.HALF_UP);
        return monthly.min(openingValue.subtract(salvageValue).max(BigDecimal.ZERO));
    }
}
