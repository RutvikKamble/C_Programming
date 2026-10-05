package com.example.assets_management.preventive_maintenance.repository;

import com.example.assets_management.preventive_maintenance.entity.PreventiveMaintenanceSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PreventiveMaintenanceScheduleRepository extends JpaRepository<PreventiveMaintenanceSchedule, UUID> {

    List<PreventiveMaintenanceSchedule> findByAsset_Id(UUID assetId);

    List<PreventiveMaintenanceSchedule> findByActiveTrueAndNextDueDateLessThanEqual(LocalDate date);

    List<PreventiveMaintenanceSchedule> findByActiveTrueAndNextDueDateBetween(LocalDate from, LocalDate to);

    List<PreventiveMaintenanceSchedule> findByActiveTrueAndNextDueDateBefore(LocalDate date);

    @Query("""
           SELECT s FROM PreventiveMaintenanceSchedule s
           WHERE s.active = true
             AND s.nextDueDate <= :date
           ORDER BY s.nextDueDate ASC
           """)
    List<PreventiveMaintenanceSchedule> findActiveDueSchedules(@Param("date") LocalDate date);

    Optional<PreventiveMaintenanceSchedule> findByIdAndActiveTrue(UUID id);
}
