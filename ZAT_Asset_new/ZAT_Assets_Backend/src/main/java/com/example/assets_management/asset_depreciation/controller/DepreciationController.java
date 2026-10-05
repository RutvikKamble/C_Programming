package com.example.assets_management.asset_depreciation.controller;

import com.example.assets_management.asset_depreciation.dto.request.DepreciationPreviewRequest;
import com.example.assets_management.asset_depreciation.dto.response.*;
import com.example.assets_management.asset_depreciation.service.DepreciationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class DepreciationController {
    private final DepreciationService depreciationService;

    @PreAuthorize("hasAuthority('Asset.Depreciation.View')")
    @GetMapping("/assets/{assetId}/depreciation")
    public ResponseEntity<List<DepreciationResponse>> history(@PathVariable UUID assetId) {
        return ResponseEntity.ok(depreciationService.getHistory(assetId));
    }

    @PreAuthorize("hasAuthority('Asset.Depreciation.View')")
    @GetMapping("/assets/{assetId}/depreciation/summary")
    public ResponseEntity<DepreciationSummaryResponse> summary(@PathVariable UUID assetId) {
        return ResponseEntity.ok(depreciationService.getSummary(assetId));
    }

    @PreAuthorize("hasAuthority('Asset.Depreciation.Calculate')")
    @PostMapping("/depreciation/preview")
    public ResponseEntity<DepreciationPreviewResponse> preview(@Valid @RequestBody DepreciationPreviewRequest request) {
        return ResponseEntity.ok(depreciationService.preview(request));
    }

    @PreAuthorize("hasAuthority('Asset.Depreciation.Calculate')")
    @PostMapping("/depreciation/run")
    public ResponseEntity<String> run(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodMonth) {
        int count = depreciationService.run(periodMonth == null ? LocalDate.now() : periodMonth);
        return ResponseEntity.ok("Depreciation processed successfully for " + count + " asset(s)");
    }

    @PreAuthorize("hasAuthority('Asset.Depreciation.View')")
    @GetMapping("/depreciation/report")
    public ResponseEntity<List<DepreciationResponse>> report(
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) UUID branchId,
            @RequestParam(required = false) UUID departmentId,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID assetId,
            @RequestParam(required = false) String method,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodMonth) {
        return ResponseEntity.ok(depreciationService.report(companyId, branchId, departmentId, categoryId, assetId, method, periodMonth));
    }
}
