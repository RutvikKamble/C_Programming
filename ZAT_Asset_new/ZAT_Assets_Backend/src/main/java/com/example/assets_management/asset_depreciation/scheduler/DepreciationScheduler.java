package com.example.assets_management.asset_depreciation.scheduler;

import com.example.assets_management.asset_depreciation.service.DepreciationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DepreciationScheduler {
    private final DepreciationService depreciationService;

    @Scheduled(cron = "${DEPRECIATION_CRON:0 15 1 * * *}")
    public void runMonthlyDepreciation() {
        depreciationService.run(LocalDate.now());
    }
}
