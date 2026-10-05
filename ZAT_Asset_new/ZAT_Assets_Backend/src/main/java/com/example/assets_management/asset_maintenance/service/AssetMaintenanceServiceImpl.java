package com.example.assets_management.asset_maintenance.service;

import com.example.assets_management.asset_maintenance.entity.AssetMaintenance;
import com.example.assets_management.asset_maintenance.repository.AssetMaintenanceRepository;
import com.example.assets_management.exception.ResourceNotFoundException;
import com.example.assets_management.asset_history.service.AssetHistoryService;
import com.example.assets_management.asset_history.enums.AssetHistoryEventType;
import com.example.assets_management.asset_master.enums.AssetStatus;
import com.example.assets_management.asset_master.repository.AssetRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AssetMaintenanceServiceImpl implements AssetMaintenanceService {

    private final AssetMaintenanceRepository maintenanceRepository;
    private final AssetHistoryService historyService;
    private final AssetRepository assetRepository;

    public AssetMaintenanceServiceImpl(AssetMaintenanceRepository maintenanceRepository,
                                       AssetHistoryService historyService,
                                       AssetRepository assetRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.historyService = historyService;
        this.assetRepository = assetRepository;
    }

    @Override
    public AssetMaintenance createMaintenance(AssetMaintenance maintenance) {
        AssetMaintenance saved = maintenanceRepository.save(maintenance);
        if (saved.getAsset() != null) {
            AssetStatus old = saved.getAsset().getStatus();
            if (old == AssetStatus.AVAILABLE || old == AssetStatus.ASSIGNED) {
                saved.getAsset().setStatus(AssetStatus.IN_REPAIR);
                assetRepository.save(saved.getAsset());
                historyService.recordEvent(saved.getAsset().getId(), AssetHistoryEventType.MAINTENANCE_CREATED, "status", old.name(), "IN_REPAIR", "Maintenance scheduled/created", "SYSTEM");
            }
        }
        return saved;
    }

    @Override
    public AssetMaintenance updateMaintenance(Long id, AssetMaintenance maintenance) {
        AssetMaintenance existing = getExisting(id);

        existing.setAsset(maintenance.getAsset());
        existing.setMaintenanceType(maintenance.getMaintenanceType());
        existing.setScheduledDate(maintenance.getScheduledDate());
        existing.setCompletedDate(maintenance.getCompletedDate());
        existing.setPerformedBy(maintenance.getPerformedBy());
        existing.setCost(maintenance.getCost());
        existing.setStatus(maintenance.getStatus());
        existing.setRemarks(maintenance.getRemarks());
        existing.setNextDueDate(maintenance.getNextDueDate());

        AssetMaintenance saved = maintenanceRepository.save(existing);
        syncAssetStatus(saved);
        return saved;
    }

    @Override
    public AssetMaintenance patchMaintenance(Long id, AssetMaintenance maintenance) {
        AssetMaintenance existing = getExisting(id);

        // Update only non‑null fields from the incoming object
        if (maintenance.getAsset() != null) {
            existing.setAsset(maintenance.getAsset());
        }
        if (maintenance.getMaintenanceType() != null) {
            existing.setMaintenanceType(maintenance.getMaintenanceType());
        }
        if (maintenance.getScheduledDate() != null) {
            existing.setScheduledDate(maintenance.getScheduledDate());
        }
        if (maintenance.getCompletedDate() != null) {
            existing.setCompletedDate(maintenance.getCompletedDate());
        }
        if (maintenance.getPerformedBy() != null) {
            existing.setPerformedBy(maintenance.getPerformedBy());
        }
        if (maintenance.getCost() != null) {
            existing.setCost(maintenance.getCost());
        }
        if (maintenance.getStatus() != null) {
            existing.setStatus(maintenance.getStatus());
        }
        if (maintenance.getRemarks() != null) {
            existing.setRemarks(maintenance.getRemarks());
        }
        if (maintenance.getNextDueDate() != null) {
            existing.setNextDueDate(maintenance.getNextDueDate());
        }

        AssetMaintenance saved = maintenanceRepository.save(existing);
        syncAssetStatus(saved);
        return saved;
    }

    @Override
    public void bulkDelete(List<Long> ids) {
        List<AssetMaintenance> toDelete = maintenanceRepository.findAllById(ids);
        if (toDelete.isEmpty()) {
            throw new ResourceNotFoundException("No maintenance records found for the given IDs");
        }
        maintenanceRepository.deleteAll(toDelete);
    }

    @Override
    public List<AssetMaintenance> bulkUpdate(List<Long> ids, AssetMaintenance updates) {
        List<AssetMaintenance> updatedList = new ArrayList<>();
        for (Long id : ids) {
            updatedList.add(patchMaintenance(id, updates));
        }
        return updatedList;
    }

    @Override
    public void deleteMaintenance(Long id) {
        if (!maintenanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("AssetMaintenance not found with id: " + id);
        }
        maintenanceRepository.deleteById(id);
    }

    @Override
    public AssetMaintenance getMaintenanceById(Long id) {
        return getExisting(id);
    }

    @Override
    public List<AssetMaintenance> getAllMaintenance() {
        return maintenanceRepository.findAll();
    }

    @Override
    public List<AssetMaintenance> getMaintenanceByAssetId(UUID assetId) {
        return maintenanceRepository.findByAsset_Id(assetId);
    }

    @Override
    public AssetMaintenance getUpcomingByAssetId(UUID assetId) {
        // Statuses considered "upcoming": Scheduled or InProgress
        List<String> statuses = List.of("Scheduled", "InProgress");
        return maintenanceRepository.findFirstByAsset_IdAndStatusInOrderByScheduledDateAsc(assetId, statuses)
                .orElseThrow(() -> new ResourceNotFoundException("No upcoming maintenance found for asset id: " + assetId));
    }

    @Override
    public AssetMaintenance getCompletedByAssetId(UUID assetId) {
        // Returns the most recent completed record for the asset
        return maintenanceRepository.findFirstByAsset_IdAndStatusOrderByCompletedDateDesc(assetId, "Completed")
                .orElseThrow(() -> new ResourceNotFoundException("No completed maintenance found for asset id: " + assetId));
    }

    // Helper to fetch existing record or throw exception
    private AssetMaintenance getExisting(Long id) {
        return maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AssetMaintenance not found with id: " + id));
    }
    private void syncAssetStatus(AssetMaintenance maintenance) {
        if (maintenance.getAsset() == null) return;
        String status = maintenance.getStatus();
        AssetStatus old = maintenance.getAsset().getStatus();
        if ("Completed".equalsIgnoreCase(status) || "Cancelled".equalsIgnoreCase(status)) {
            if (old == AssetStatus.IN_REPAIR || old == AssetStatus.REPAIRED) {
                maintenance.getAsset().setStatus(AssetStatus.AVAILABLE);
                assetRepository.save(maintenance.getAsset());
                historyService.recordEvent(maintenance.getAsset().getId(), AssetHistoryEventType.MAINTENANCE_COMPLETED, "status", old.name(), "AVAILABLE", "Maintenance completed", "SYSTEM");
            }
        } else if ("Scheduled".equalsIgnoreCase(status) || "InProgress".equalsIgnoreCase(status)) {
            if (old == AssetStatus.AVAILABLE || old == AssetStatus.ASSIGNED) {
                maintenance.getAsset().setStatus(AssetStatus.IN_REPAIR);
                assetRepository.save(maintenance.getAsset());
                historyService.recordEvent(maintenance.getAsset().getId(), AssetHistoryEventType.MAINTENANCE_STATUS_CHANGED, "status", old.name(), "IN_REPAIR", "Asset placed under maintenance", "SYSTEM");
            }
        }
    }

}
