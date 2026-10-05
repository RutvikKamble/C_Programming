package com.example.assets_management.alerts.service;

import com.example.assets_management.alerts.entity.AssetAlert;
import com.example.assets_management.alerts.repository.AssetAlertRepository;
import com.example.assets_management.asset_maintenance.repository.AssetMaintenanceRepository;
import com.example.assets_management.asset_warranty.entity.AssetWarranty;
import com.example.assets_management.asset_warranty.repository.AssetWarrantyRepository;
import com.example.assets_management.asset_software.entity.SoftwareLicense;
import com.example.assets_management.asset_software.repository.SoftwareLicenseRepository;
import com.example.assets_management.amc.entity.AmcContract;
import com.example.assets_management.amc.repository.AmcContractRepository;
import com.example.assets_management.preventive_maintenance.repository.PreventiveMaintenanceScheduleRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.*;

@Service
public class AlertSchedulerService {
    private final AssetAlertRepository alerts;
    private final AssetWarrantyRepository warranties;
    private final AssetMaintenanceRepository maintenance;
    private final PreventiveMaintenanceScheduleRepository schedules;
    private final AmcContractRepository amcContracts;
    private final SoftwareLicenseRepository softwareLicenses;

    public AlertSchedulerService(AssetAlertRepository alerts, AssetWarrantyRepository warranties, AssetMaintenanceRepository maintenance, PreventiveMaintenanceScheduleRepository schedules, AmcContractRepository amcContracts, SoftwareLicenseRepository softwareLicenses) {
        this.alerts = alerts;
        this.warranties = warranties;
        this.maintenance = maintenance;
        this.schedules = schedules;
        this.amcContracts = amcContracts;
        this.softwareLicenses = softwareLicenses;
    }

    @Scheduled(cron = "${ALERT_CRON:0 0 8 * * *}")
    public void scheduled() {
        generate();
    }

    public int generate() {
        int n = 0;
        LocalDate today = LocalDate.now(), horizon = today.plusDays(30);
        for (AssetWarranty w : warranties.findByEndDateBetween(today, horizon)) {
            String k = "WARRANTY:" + w.getId();
            if (!alerts.existsByReferenceKey(k)) {
                AssetAlert a = new AssetAlert();
                a.setAsset(w.getAsset());
                a.setType("WARRANTY_EXPIRY");
                a.setDueDate(w.getEndDate());
                a.setMessage("Warranty expires on " + w.getEndDate());
                a.setReferenceKey(k);
                alerts.save(a);
                n++;
            }
        }
        for (AmcContract contract : amcContracts.findByRenewalDateBetweenOrderByRenewalDateAsc(today, horizon)) {
            String k = "AMC:" + contract.getId() + ":" + contract.getRenewalDate();
            if (!alerts.existsByReferenceKey(k)) {
                AssetAlert a = new AssetAlert();
                a.setAsset(contract.getAsset());
                a.setType("AMC_RENEWAL");
                a.setDueDate(contract.getRenewalDate());
                a.setMessage("AMC contract renewal is due on " + contract.getRenewalDate());
                a.setReferenceKey(k);
                alerts.save(a);
                n++;
            }
        }
        for (SoftwareLicense license : softwareLicenses.findByExpiryDateBetweenOrderByExpiryDateAsc(today, horizon)) {
            String k = "SOFTWARE_LICENSE:" + license.getId() + ":" + license.getExpiryDate();
            if (!alerts.existsByReferenceKey(k)) {
                AssetAlert a = new AssetAlert();
                a.setAsset(license.getAsset());
                a.setType("SOFTWARE_LICENSE_EXPIRY");
                a.setDueDate(license.getExpiryDate());
                a.setMessage("Software license expires on " + license.getExpiryDate() + " (" + license.getSeatsUsed() + "/" + license.getSeatCount() + " seats used)");
                a.setReferenceKey(k);
                alerts.save(a);
                n++;
            }
        }

        for (var s : schedules.findByActiveTrueAndNextDueDateLessThanEqual(horizon)) {
            String k = "PM:" + s.getId() + ":" + s.getNextDueDate();
            if (!alerts.existsByReferenceKey(k)) {
                AssetAlert a = new AssetAlert();
                a.setAsset(s.getAsset());
                a.setType(s.getNextDueDate().isBefore(today) ? "PM_OVERDUE" : "PM_DUE");
                a.setDueDate(s.getNextDueDate());
                a.setMessage((s.getNextDueDate().isBefore(today) ? "Preventive maintenance overdue: " : "Preventive maintenance due: ") + s.getScheduleName());
                a.setReferenceKey(k);
                alerts.save(a);
                n++;
            }
        }
        for (var m : maintenance.findAll()) {
            if (m.getScheduledDate() != null && m.getCompletedDate() == null && m.getScheduledDate().isBefore(today) && !"Completed".equalsIgnoreCase(m.getStatus()) && !"Cancelled".equalsIgnoreCase(m.getStatus())) {
                String k = "MAINTENANCE:" + m.getId();
                if (!alerts.existsByReferenceKey(k)) {
                    AssetAlert a = new AssetAlert();
                    a.setAsset(m.getAsset());
                    a.setType("MAINTENANCE_OVERDUE");
                    a.setDueDate(m.getScheduledDate());
                    a.setMessage("Maintenance is overdue");
                    a.setReferenceKey(k);
                    alerts.save(a);
                    n++;
                }
            }
        }
        return n;
    }
}
