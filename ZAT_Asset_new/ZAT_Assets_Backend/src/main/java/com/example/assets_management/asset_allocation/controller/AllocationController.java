package com.example.assets_management.asset_allocation.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.example.assets_management.asset_allocation.entity.Allocation;
import com.example.assets_management.asset_allocation.service.AllocationService;
import com.example.assets_management.asset_allocation.dto.response.AllocationResponse;

import com.example.assets_management.asset_allocation.dto.request.AllocateAssetRequest;
import com.example.assets_management.asset_allocation.dto.request.BulkDeleteRequest;
import com.example.assets_management.asset_allocation.dto.request.BulkUpdateRequest;
import com.example.assets_management.asset_allocation.dto.request.DeallocateAssetRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/allocations")
@RequiredArgsConstructor
public class AllocationController {

    private final AllocationService allocationService;

    // ================= CRUD =================

    @PreAuthorize("hasAuthority(\'Asset.Asset_Allocation.View\')")
    @GetMapping
    public List<AllocationResponse> getAll() {
        return allocationService.findAll();
    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Allocation.View\')")
    @GetMapping("/{id}")
    public ResponseEntity<Allocation> getById(@PathVariable UUID id) {

        return allocationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Allocation.Create\')")
    @PostMapping
    public Allocation save(@RequestBody Allocation allocation) {

        return allocationService.save(allocation);

    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Allocation.Update\')")
    @PutMapping("/{id}")
    public ResponseEntity<Allocation> update(
            @PathVariable UUID id,
            @RequestBody Allocation allocation) {

        return ResponseEntity.ok(
                allocationService.update(id, allocation)
        );

    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Allocation.Delete\')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id) {

        allocationService.delete(id);

        return ResponseEntity.noContent().build();

    }

    // ================= Allocate =================

    @PreAuthorize("hasAuthority(\'Asset.Asset_Allocation.Allocate\')")
    @PostMapping("/allocate")
    public ResponseEntity<Allocation> allocate(
            @RequestBody AllocateAssetRequest request) {

        return ResponseEntity.ok(
                allocationService.allocate(request)
        );

    }

    // ================= Deallocate =================

    @PreAuthorize("hasAuthority(\'Asset.Asset_Allocation.Deallocate\')")
    @PostMapping("/deallocate")
    public ResponseEntity<Allocation> deallocate(
            @RequestBody DeallocateAssetRequest request) {

        return ResponseEntity.ok(
                allocationService.deallocate(request)
        );

    }

    // ================= Current Allocation =================

    @PreAuthorize("hasAuthority(\'Asset.Asset_Allocation.View\')")
    @GetMapping("/asset/{assetId}")
    public ResponseEntity<Allocation> getCurrentAllocation(
            @PathVariable UUID assetId) {

        return ResponseEntity.ok(
                allocationService.getCurrentAllocation(assetId)
        );

    }

    // ================= Allocation History =================

    @PreAuthorize("hasAuthority(\'Asset.Asset_Allocation.View\')")
    @GetMapping("/history/{assetId}")
    public ResponseEntity<List<Allocation>> history(
            @PathVariable UUID assetId) {

        return ResponseEntity.ok(
                allocationService.getHistory(assetId)
        );

    }

    // ================= Category =================

    @PreAuthorize("hasAuthority(\'Asset.Asset_Allocation.View\')")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Allocation>> byCategory(
            @PathVariable UUID categoryId) {

        return ResponseEntity.ok(
                allocationService.getByCategory(categoryId)
        );

    }

    // ================= Allocation Details =================

    @PreAuthorize("hasAuthority(\'Asset.Asset_Allocation.View\')")
    @GetMapping("/details/{allocationId}")
    public ResponseEntity<Allocation> details(
            @PathVariable UUID allocationId) {

        return ResponseEntity.ok(
                allocationService.getAllocationDetails(allocationId)
        );

    }

    // ================= PATCH =================

    @PreAuthorize("hasAuthority(\'Asset.Asset_Allocation.Update\')")
    @PatchMapping("/{id}")
    public ResponseEntity<Allocation> patch(
            @PathVariable UUID id,
            @RequestBody Map<String,Object> updates) {

        return ResponseEntity.ok(
                allocationService.patch(id, updates)
        );

    }

    // ================= Bulk Delete =================

    @PreAuthorize("hasAuthority(\'Asset.Asset_Allocation.Delete\')")
    @DeleteMapping("/bulk")
    public ResponseEntity<String> bulkDelete(
            @RequestBody BulkDeleteRequest request) {

        allocationService.bulkDelete(request.getIds());

        return ResponseEntity.ok("Allocations deleted successfully.");

    }

    // ================= Bulk Update =================

    @PreAuthorize("hasAuthority(\'Asset.Asset_Allocation.Update\')")
    @PutMapping("/bulk")
    public ResponseEntity<List<Allocation>> bulkUpdate(
            @RequestBody BulkUpdateRequest request) {

        return ResponseEntity.ok(
                allocationService.bulkUpdate(
                        request.getAllocations()
                )
        );

    }

    /*
    // ================= Import =================

    @PostMapping(
            value = "/import",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> importAllocation(
            @RequestParam MultipartFile file)
            throws IOException {

        allocationService.importAllocation(file);

        return ResponseEntity.ok("Import Successful.");

    }

    // ================= Export =================

    @GetMapping("/export")
    public void exportAllocation(
            HttpServletResponse response)
            throws IOException {

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );

        response.setHeader(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=allocations.xlsx"
        );

        allocationService.exportAllocation(response);

    }
    */
}