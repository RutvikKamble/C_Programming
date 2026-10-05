package com.example.assets_management.asset_maintenance.repository;

import com.example.assets_management.asset_maintenance.entity.AssetMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetMaintenanceRepository extends JpaRepository<AssetMaintenance, Long> {

    // Fetch full maintenance history for one asset
    List<AssetMaintenance> findByAsset_Id(UUID assetId);

    // Fetch the soonest upcoming maintenance (by scheduledDate) for a given asset and status list
    Optional<AssetMaintenance> findFirstByAsset_IdAndStatusInOrderByScheduledDateAsc(UUID assetId, List<String> statuses);

    // Fetch the most recent completed maintenance (by completedDate descending) for a given asset
    Optional<AssetMaintenance> findFirstByAsset_IdAndStatusOrderByCompletedDateDesc(UUID assetId, String status);

    boolean existsByPreventiveMaintenanceScheduleIdAndScheduledDate(UUID scheduleId, java.time.LocalDate scheduledDate);

    Optional<AssetMaintenance> findFirstByPreventiveMaintenanceScheduleIdAndScheduledDateOrderByIdDesc(
            UUID scheduleId, java.time.LocalDate scheduledDate);

    Optional<AssetMaintenance> findFirstByPreventiveMaintenanceScheduleIdAndStatusOrderByCompletedDateDesc(
            UUID scheduleId, String status);

    List<AssetMaintenance> findByPreventiveMaintenanceScheduleIdOrderByScheduledDateDesc(UUID scheduleId);
}
