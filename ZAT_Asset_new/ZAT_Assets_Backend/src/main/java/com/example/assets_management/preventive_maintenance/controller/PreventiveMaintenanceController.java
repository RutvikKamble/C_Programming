package com.example.assets_management.preventive_maintenance.controller;

import com.example.assets_management.asset_maintenance.entity.AssetMaintenance;
import com.example.assets_management.asset_master.repository.AssetRepository;
import com.example.assets_management.exception.ResourceNotFoundException;
import com.example.assets_management.preventive_maintenance.entity.PreventiveMaintenanceSchedule;
import com.example.assets_management.preventive_maintenance.repository.PreventiveMaintenanceScheduleRepository;
import com.example.assets_management.preventive_maintenance.service.PreventiveMaintenanceAutomationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/maintenance-schedules")
public class PreventiveMaintenanceController {

    private final PreventiveMaintenanceScheduleRepository repository;
    private final AssetRepository assetRepository;
    private final PreventiveMaintenanceAutomationService automationService;

    public PreventiveMaintenanceController(
            PreventiveMaintenanceScheduleRepository repository,
            AssetRepository assetRepository,
            PreventiveMaintenanceAutomationService automationService) {
        this.repository = repository;
        this.assetRepository = assetRepository;
        this.automationService = automationService;
    }

    @PreAuthorize("hasAuthority('Asset.Preventive_Maintenance.View')")
    @GetMapping
    public List<PreventiveMaintenanceSchedule> all() {
        return repository.findAll();
    }

    @PreAuthorize("hasAuthority('Asset.Preventive_Maintenance.View')")
    @GetMapping("/{id}")
    public PreventiveMaintenanceSchedule one(@PathVariable UUID id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Maintenance schedule not found: " + id));
    }

    @PreAuthorize("hasAuthority('Asset.Preventive_Maintenance.View')")
    @GetMapping("/asset/{assetId}")
    public List<PreventiveMaintenanceSchedule> byAsset(
            @PathVariable UUID assetId) {
        return repository.findByAsset_Id(assetId);
    }

    @PreAuthorize("hasAuthority('Asset.Preventive_Maintenance.View')")
    @GetMapping("/due")
    public List<PreventiveMaintenanceSchedule> due() {
        return repository.findActiveDueSchedules(LocalDate.now());
    }

    @PreAuthorize("hasAuthority('Asset.Preventive_Maintenance.View')")
    @GetMapping("/upcoming")
    public List<PreventiveMaintenanceSchedule> upcoming(
            @RequestParam(defaultValue = "30") int days) {
        return automationService.getUpcoming(days);
    }

    @PreAuthorize("hasAuthority('Asset.Preventive_Maintenance.View')")
    @GetMapping("/overdue")
    public List<PreventiveMaintenanceSchedule> overdue() {
        return automationService.getOverdue();
    }

    @PreAuthorize("hasAuthority('Asset.Preventive_Maintenance.Create')")
    @PostMapping
    public ResponseEntity<PreventiveMaintenanceSchedule> create(
            @RequestBody PreventiveMaintenanceSchedule schedule) {

        schedule.setId(null);

        if (schedule.getAsset() == null ||
                schedule.getAsset().getId() == null) {
            throw new IllegalArgumentException("asset.id is required");
        }

        schedule.setAsset(
                assetRepository.findById(schedule.getAsset().getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Asset not found: "
                                                + schedule.getAsset().getId()))
        );

        if (schedule.getFrequencyDays() == null ||
                schedule.getFrequencyDays() <= 0) {
            throw new IllegalArgumentException(
                    "frequencyDays must be greater than zero");
        }

        if (schedule.getNextDueDate() == null) {
            throw new IllegalArgumentException(
                    "nextDueDate is required");
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(repository.save(schedule));
    }

    @PreAuthorize("hasAuthority('Asset.Preventive_Maintenance.Update')")
    @PutMapping("/{id}")
    public PreventiveMaintenanceSchedule update(
            @PathVariable UUID id,
            @RequestBody PreventiveMaintenanceSchedule input) {

        PreventiveMaintenanceSchedule existing = one(id);

        if (input.getAsset() != null &&
                input.getAsset().getId() != null) {

            existing.setAsset(
                    assetRepository.findById(input.getAsset().getId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Asset not found: "
                                                    + input.getAsset().getId()))
            );
        }

        if (input.getScheduleName() != null) {
            existing.setScheduleName(input.getScheduleName());
        }

        if (input.getFrequencyDays() != null) {
            if (input.getFrequencyDays() <= 0) {
                throw new IllegalArgumentException(
                        "frequencyDays must be greater than zero");
            }

            existing.setFrequencyDays(input.getFrequencyDays());
        }

        if (input.getLastDoneDate() != null) {
            existing.setLastDoneDate(input.getLastDoneDate());
        }

        if (input.getNextDueDate() != null) {
            existing.setNextDueDate(input.getNextDueDate());
        }

        existing.setAutoGenerateWorkOrder(
                input.isAutoGenerateWorkOrder());

        existing.setActive(input.isActive());

        if (input.getRemarks() != null) {
            existing.setRemarks(input.getRemarks());
        }

        return repository.save(existing);
    }

    /**
     * Manual completion of a PM schedule is retained for backward compatibility.
     * The preferred production flow is to complete the generated AssetMaintenance record,
     * which then updates the PM schedule automatically.
     */
    @PreAuthorize("hasAuthority('Asset.Preventive_Maintenance.Complete')")
    @PatchMapping("/{id}/complete")
    public PreventiveMaintenanceSchedule complete(
            @PathVariable UUID id,
            @RequestParam(required = false) LocalDate completedDate) {

        PreventiveMaintenanceSchedule schedule = one(id);

        if (schedule.getFrequencyDays() == null ||
                schedule.getFrequencyDays() <= 0) {
            throw new IllegalStateException(
                    "frequencyDays must be greater than zero");
        }

        LocalDate done = completedDate != null
                ? completedDate
                : LocalDate.now();

        schedule.setLastDoneDate(done);

        schedule.setNextDueDate(
                done.plusDays(schedule.getFrequencyDays()));

        return repository.save(schedule);
    }

    @PreAuthorize("hasAuthority('Asset.Preventive_Maintenance.Create')")
    @PostMapping("/{id}/generate-maintenance")
    public ResponseEntity<AssetMaintenance> generateMaintenance(
            @PathVariable UUID id) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(automationService.generateMaintenance(id));
    }

    @PreAuthorize("hasAuthority('Asset.Preventive_Maintenance.View')")
    @GetMapping("/{id}/maintenance")
    public List<AssetMaintenance> maintenanceForSchedule(
            @PathVariable UUID id) {

        return automationService.getMaintenanceHistory(id);
    }

    /**
     * Manually trigger the same automation used by the scheduled job.
     */
    @PreAuthorize("hasAuthority('Asset.Preventive_Maintenance.Create')")
    @PostMapping("/run")
    public ResponseEntity<String> runAutomation() {

        int generated = automationService.runAutomation();

        return ResponseEntity.ok(
                "Preventive maintenance automation completed. "
                        + "Generated "
                        + generated
                        + " maintenance record(s).");
    }

    @PreAuthorize("hasAuthority('Asset.Preventive_Maintenance.Delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id) {

        repository.delete(one(id));

        return ResponseEntity.noContent().build();
    }
}