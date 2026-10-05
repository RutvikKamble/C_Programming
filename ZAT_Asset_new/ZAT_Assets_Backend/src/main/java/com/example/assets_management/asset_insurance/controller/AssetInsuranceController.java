package com.example.assets_management.asset_insurance.controller;

import com.example.assets_management.asset_insurance.dto.request.InsuranceCreateRequest;
import com.example.assets_management.asset_insurance.dto.request.InsuranceUpdateRequest;
import com.example.assets_management.asset_insurance.dto.response.InsuranceResponse;
import com.example.assets_management.asset_insurance.enums.InsuranceStatus;
import com.example.assets_management.asset_insurance.service.AssetInsuranceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/insurance-policies")
@RequiredArgsConstructor
public class AssetInsuranceController {

    private final AssetInsuranceService insuranceService;

    @PreAuthorize("hasAuthority('Asset.Insurance.View')")
    @GetMapping
    public ResponseEntity<List<InsuranceResponse>> getAll(
            @RequestParam(required = false) InsuranceStatus status) {
        return ResponseEntity.ok(insuranceService.findAll(status));
    }

    @PreAuthorize("hasAuthority('Asset.Insurance.View')")
    @GetMapping("/{id}")
    public ResponseEntity<InsuranceResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(insuranceService.findById(id));
    }

    @PreAuthorize("hasAuthority('Asset.Insurance.Create')")
    @PostMapping
    public ResponseEntity<InsuranceResponse> create(@Valid @RequestBody InsuranceCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(insuranceService.create(request));
    }

    @PreAuthorize("hasAuthority('Asset.Insurance.Update')")
    @PatchMapping("/{id}")
    public ResponseEntity<InsuranceResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody InsuranceUpdateRequest request) {
        return ResponseEntity.ok(insuranceService.update(id, request));
    }

    @PreAuthorize("hasAuthority('Asset.Insurance.Delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        insuranceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('Asset.Insurance.View')")
    @GetMapping("/asset/{assetId}")
    public ResponseEntity<List<InsuranceResponse>> getByAsset(@PathVariable UUID assetId) {
        return ResponseEntity.ok(insuranceService.findByAssetId(assetId));
    }

    @PreAuthorize("hasAuthority('Asset.Insurance.View')")
    @GetMapping("/expiring-soon")
    public ResponseEntity<List<InsuranceResponse>> getExpiringSoon(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(insuranceService.findExpiringSoon(days));
    }
}
