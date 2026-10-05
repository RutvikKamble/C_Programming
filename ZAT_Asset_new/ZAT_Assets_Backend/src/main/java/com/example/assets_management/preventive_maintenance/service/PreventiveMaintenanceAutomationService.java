package com.example.assets_management.preventive_maintenance.service;

import com.example.assets_management.asset_maintenance.entity.AssetMaintenance;
import com.example.assets_management.preventive_maintenance.entity.PreventiveMaintenanceSchedule;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PreventiveMaintenanceAutomationService {

    List<PreventiveMaintenanceSchedule> getUpcoming(int days);

    List<PreventiveMaintenanceSchedule> getOverdue();

    AssetMaintenance generateMaintenance(UUID scheduleId);

    List<AssetMaintenance> getMaintenanceHistory(UUID scheduleId);

    int runAutomation();

    void synchronizeCompletion(UUID scheduleId, LocalDate completedDate);
}
