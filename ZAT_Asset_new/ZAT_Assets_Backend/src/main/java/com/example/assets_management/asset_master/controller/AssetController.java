package com.example.assets_management.asset_master.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.asset_master.service.AssetService;

import com.example.assets_management.asset_master.dto.request.BulkDeleteRequest;
import com.example.assets_management.asset_master.dto.request.BulkUpdateRequest;
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
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    // -------------------- CRUD --------------------

    @PreAuthorize("hasAuthority(\'Asset.Asset_Master.View\')")
    @GetMapping
    public List<Asset> getAllAssets() {
        return assetService.findAll();
    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Master.View\')")
    @GetMapping("/{id}")
    public ResponseEntity<Asset> getAssetById(@PathVariable UUID id) {

        return assetService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Master.Create\')")
    @PostMapping
    public ResponseEntity<Asset> createAsset(@RequestBody Asset asset) {

        return ResponseEntity.ok(assetService.save(asset));

    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Master.Update\')")
    @PutMapping("/{id}")
    public ResponseEntity<Asset> updateAsset(
            @PathVariable UUID id,
            @RequestBody Asset asset) {

        return ResponseEntity.ok(assetService.update(id, asset));

    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Master.Delete\')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsset(@PathVariable UUID id) {

        assetService.delete(id);

        return ResponseEntity.noContent().build();

    }

    // -------------------- PATCH --------------------

    @PreAuthorize("hasAuthority(\'Asset.Asset_Master.Update\')")
    @PatchMapping("/{id}")
    public ResponseEntity<Asset> patchAsset(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> updates) {

        return ResponseEntity.ok(assetService.patch(id, updates));

    }

    // -------------------- BULK DELETE --------------------

    @PreAuthorize("hasAuthority(\'Asset.Asset_Master.Delete\')")
    @DeleteMapping("/bulk")
    public ResponseEntity<String> bulkDelete(
            @RequestBody BulkDeleteRequest request) {

        assetService.bulkDelete(request.getIds());

        return ResponseEntity.ok("Assets deleted successfully.");

    }

    // -------------------- BULK UPDATE --------------------

    @PreAuthorize("hasAuthority(\'Asset.Asset_Master.Update\')")
    @PutMapping("/bulk")
    public ResponseEntity<List<Asset>> bulkUpdate(
            @RequestBody BulkUpdateRequest request) {

        return ResponseEntity.ok(
                assetService.bulkUpdate(request.getAssets())
        );

    }

    // -------------------- IMPORT --------------------

    @PreAuthorize("hasAuthority(\'Asset.Asset_Master.Import\')")
    @PostMapping(
            value = "/import",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<String> importAssets(
            @RequestParam("file") MultipartFile file)
            throws IOException {

        assetService.importAssets(file);

        return ResponseEntity.ok("Assets imported successfully.");

    }

    // -------------------- EXPORT --------------------

    @PreAuthorize("hasAuthority(\'Asset.Asset_Master.Export\')")
    @GetMapping("/export")
    public void exportAssets(HttpServletResponse response)
            throws IOException {

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );

        response.setHeader(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=assets.xlsx"
        );

        assetService.exportAssets(response);

    }

}
