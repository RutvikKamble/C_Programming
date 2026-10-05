package com.example.assets_management.preventive_maintenance.service;

import com.example.assets_management.asset_history.dto.request.AssetHistoryRequest;
import com.example.assets_management.asset_history.service.AssetHistoryService;
import com.example.assets_management.asset_maintenance.entity.AssetMaintenance;
import com.example.assets_management.asset_maintenance.enums.MaintenanceType;
import com.example.assets_management.asset_maintenance.repository.AssetMaintenanceRepository;
import com.example.assets_management.asset_master.enums.AssetStatus;
import com.example.assets_management.preventive_maintenance.entity.PreventiveMaintenanceSchedule;
import com.example.assets_management.preventive_maintenance.repository.PreventiveMaintenanceScheduleRepository;
import com.example.assets_management.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PreventiveMaintenanceAutomationServiceImpl implements PreventiveMaintenanceAutomationService {

    private final PreventiveMaintenanceScheduleRepository scheduleRepository;
    private final AssetMaintenanceRepository maintenanceRepository;
    private final AssetHistoryService historyService;

    public PreventiveMaintenanceAutomationServiceImpl(
            PreventiveMaintenanceScheduleRepository scheduleRepository,
            AssetMaintenanceRepository maintenanceRepository,
            AssetHistoryService historyService) {
        this.scheduleRepository = scheduleRepository;
        this.maintenanceRepository = maintenanceRepository;
        this.historyService = historyService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PreventiveMaintenanceSchedule> getUpcoming(int days) {
        if (days < 0 || days > 365) {
            throw new IllegalArgumentException("days must be between 0 and 365");
        }
        LocalDate today = LocalDate.now();
        return scheduleRepository.findByActiveTrueAndNextDueDateBetween(today, today.plusDays(days));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PreventiveMaintenanceSchedule> getOverdue() {
        return scheduleRepository.findByActiveTrueAndNextDueDateBefore(LocalDate.now());
    }

    @Override
    @Transactional
    public AssetMaintenance generateMaintenance(UUID scheduleId) {
        PreventiveMaintenanceSchedule schedule = getActiveSchedule(scheduleId);
        validateSchedule(schedule);

        LocalDate cycleDate = schedule.getNextDueDate();
        if (maintenanceRepository.existsByPreventiveMaintenanceScheduleIdAndScheduledDate(scheduleId, cycleDate)) {
            return maintenanceRepository
                    .findFirstByPreventiveMaintenanceScheduleIdAndScheduledDateOrderByIdDesc(scheduleId, cycleDate)
                    .orElseThrow(() -> new IllegalStateException("Maintenance cycle exists but could not be loaded"));
        }

        AssetMaintenance maintenance = new AssetMaintenance();
        maintenance.setAsset(schedule.getAsset());
        maintenance.setMaintenanceType(MaintenanceType.PREVENTIVE);
        maintenance.setScheduledDate(cycleDate);
        maintenance.setStatus("Scheduled");
        maintenance.setNextDueDate(cycleDate.plusDays(schedule.getFrequencyDays()));
        maintenance.setRemarks(buildGeneratedRemark(schedule));
        maintenance.setPreventiveMaintenanceScheduleId(scheduleId);

        AssetMaintenance saved = maintenanceRepository.save(maintenance);

        historyService.create(new AssetHistoryRequest(
                schedule.getAsset().getId(),
                "PM_MAINTENANCE_GENERATED",
                "preventiveMaintenanceScheduleId",
                null,
                scheduleId.toString(),
                "Preventive maintenance automatically generated for due cycle " + cycleDate,
                "SYSTEM"));

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetMaintenance> getMaintenanceHistory(UUID scheduleId) {
        if (!scheduleRepository.existsById(scheduleId)) {
            throw new ResourceNotFoundException("Preventive maintenance schedule not found: " + scheduleId);
        }
        return maintenanceRepository.findByPreventiveMaintenanceScheduleIdOrderByScheduledDateDesc(scheduleId);
    }

    @Override
    @Transactional
    public int runAutomation() {
        LocalDate today = LocalDate.now();
        int generated = 0;

        for (PreventiveMaintenanceSchedule schedule : scheduleRepository.findActiveDueSchedules(today)) {
            if (!isAssetEligible(schedule)) {
                continue;
            }

            if (!schedule.isAutoGenerateWorkOrder()) {
                continue;
            }

            if (!maintenanceRepository.existsByPreventiveMaintenanceScheduleIdAndScheduledDate(
                    schedule.getId(), schedule.getNextDueDate())) {
                generateMaintenance(schedule.getId());
                generated++;
            }
        }

        return generated;
    }

    @Override
    @Transactional
    public void synchronizeCompletion(UUID scheduleId, LocalDate completedDate) {
        if (scheduleId == null) {
            return;
        }

        PreventiveMaintenanceSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Preventive maintenance schedule not found: " + scheduleId));

        validateSchedule(schedule);

        LocalDate doneDate = completedDate != null ? completedDate : LocalDate.now();
        LocalDate oldNextDue = schedule.getNextDueDate();
        LocalDate nextDue = doneDate.plusDays(schedule.getFrequencyDays());

        schedule.setLastDoneDate(doneDate);
        schedule.setNextDueDate(nextDue);
        scheduleRepository.save(schedule);

        historyService.create(new AssetHistoryRequest(
                schedule.getAsset().getId(),
                "PM_COMPLETED",
                "nextDueDate",
                oldNextDue == null ? null : oldNextDue.toString(),
                nextDue.toString(),
                "Preventive maintenance completed; next cycle scheduled for " + nextDue,
                "SYSTEM"));
    }

    private PreventiveMaintenanceSchedule getActiveSchedule(UUID id) {
        return scheduleRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Active preventive maintenance schedule not found: " + id));
    }

    private void validateSchedule(PreventiveMaintenanceSchedule schedule) {
        if (schedule.getAsset() == null) {
            throw new IllegalStateException("Preventive maintenance schedule asset is required");
        }
        if (schedule.getFrequencyDays() == null || schedule.getFrequencyDays() <= 0) {
            throw new IllegalStateException("frequencyDays must be greater than zero");
        }
        if (schedule.getNextDueDate() == null) {
            throw new IllegalStateException("nextDueDate is required");
        }
    }

    private boolean isAssetEligible(PreventiveMaintenanceSchedule schedule) {
        return schedule.getAsset() != null
                && schedule.getAsset().getStatus() != AssetStatus.CLOSED
                && schedule.getAsset().getStatus() != AssetStatus.LOST;
    }

    private String buildGeneratedRemark(PreventiveMaintenanceSchedule schedule) {
        String base = "Automatically generated from preventive maintenance schedule " + schedule.getId();
        return schedule.getRemarks() == null || schedule.getRemarks().isBlank()
                ? base
                : base + ": " + schedule.getRemarks();
    }
}
