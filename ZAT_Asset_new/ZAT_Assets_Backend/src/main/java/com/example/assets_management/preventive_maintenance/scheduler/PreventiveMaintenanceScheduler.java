package com.example.assets_management.preventive_maintenance.scheduler;

import com.example.assets_management.preventive_maintenance.service.PreventiveMaintenanceAutomationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PreventiveMaintenanceScheduler {

    private static final Logger log = LoggerFactory.getLogger(PreventiveMaintenanceScheduler.class);

    private final PreventiveMaintenanceAutomationService automationService;

    public PreventiveMaintenanceScheduler(PreventiveMaintenanceAutomationService automationService) {
        this.automationService = automationService;
    }

    @Scheduled(cron = "${PREVENTIVE_MAINTENANCE_CRON:0 15 8 * * *}")
    public void scheduledRun() {
        int generated = automationService.runAutomation();
        log.info("Preventive maintenance automation completed; generated {} maintenance record(s)", generated);
    }
}
