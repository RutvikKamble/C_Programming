package com.example.assets_management.asset_vehicle.controller;

import com.example.assets_management.asset_vehicle.dto.request.VehicleFuelLogRequest;
import com.example.assets_management.asset_vehicle.dto.response.VehicleFuelLogResponse;
import com.example.assets_management.asset_vehicle.service.VehicleFuelLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vehicles/{vehicleId}/fuel-logs")
@RequiredArgsConstructor
public class VehicleFuelLogController {

    private final VehicleFuelLogService fuelLogService;

    @PreAuthorize("hasAuthority('Asset.Vehicle.View')")
    @GetMapping
    public ResponseEntity<List<VehicleFuelLogResponse>> getAll(@PathVariable UUID vehicleId) {
        return ResponseEntity.ok(fuelLogService.findByVehicleId(vehicleId));
    }

    @PreAuthorize("hasAuthority('Asset.Vehicle.View')")
    @GetMapping("/{logId}")
    public ResponseEntity<VehicleFuelLogResponse> getById(
            @PathVariable UUID vehicleId,
            @PathVariable UUID logId) {
        return ResponseEntity.ok(fuelLogService.findById(vehicleId, logId));
    }

    @PreAuthorize("hasAuthority('Asset.Vehicle.Update')")
    @PostMapping
    public ResponseEntity<VehicleFuelLogResponse> create(
            @PathVariable UUID vehicleId,
            @Valid @RequestBody VehicleFuelLogRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fuelLogService.create(vehicleId, request));
    }

    @PreAuthorize("hasAuthority('Asset.Vehicle.Update')")
    @PatchMapping("/{logId}")
    public ResponseEntity<VehicleFuelLogResponse> update(
            @PathVariable UUID vehicleId,
            @PathVariable UUID logId,
            @Valid @RequestBody VehicleFuelLogRequest request) {
        return ResponseEntity.ok(fuelLogService.update(vehicleId, logId, request));
    }

    @PreAuthorize("hasAuthority('Asset.Vehicle.Delete')")
    @DeleteMapping("/{logId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID vehicleId,
            @PathVariable UUID logId) {
        fuelLogService.delete(vehicleId, logId);
        return ResponseEntity.noContent().build();
    }
}
