package com.example.assets_management.asset_tracking.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.example.assets_management.asset_tracking.service.AssetTrackingService;

import com.example.assets_management.asset_tracking.dto.request.AssetTrackingRequest;
import com.example.assets_management.asset_tracking.dto.response.AssetTrackingResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller exposing the Asset Tracking API.
 *
 * <p>Base path: {@code /api/tracking} (matches the frontend contract).</p>
 *
 * <ul>
 *   <li>GET {@code /api/tracking} ÃƒÂ¢Ã¢â€šÂ¬Ã¢â‚¬Â list, optional {@code type} filter</li>
 *   <li>GET {@code /api/tracking/{assetId}} ÃƒÂ¢Ã¢â€šÂ¬Ã¢â‚¬Â tracking events for one asset</li>
 *   <li>POST {@code /api/tracking} ÃƒÂ¢Ã¢â€šÂ¬Ã¢â‚¬Â create an event</li>
 *   <li>DELETE {@code /api/tracking/{id}} ÃƒÂ¢Ã¢â€šÂ¬Ã¢â‚¬Â delete an event</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/tracking")
public class AssetTrackingController {

    private final AssetTrackingService service;

    public AssetTrackingController(AssetTrackingService service) {
        this.service = service;
    }

    /**
     * List of all tracking events, optionally filtered by type.
     */
    @PreAuthorize("hasAuthority(\'Asset.Asset_Tracking.View\')")
    @GetMapping
    public ResponseEntity<List<AssetTrackingResponse>> getAll(
            @RequestParam(required = false) String type) {

        return ResponseEntity.ok(service.findAll(type));
    }

    /**
     * Tracking events for a single asset, newest first.
     */
    @PreAuthorize("hasAuthority(\'Asset.Asset_Tracking.View\')")
    @GetMapping("/{assetId}")
    public ResponseEntity<List<AssetTrackingResponse>> getByAssetId(@PathVariable UUID assetId) {
        return ResponseEntity.ok(service.findByAssetId(assetId));
    }

    /**
     * Create a new tracking event.
     */
    @PreAuthorize("hasAuthority(\'Asset.Asset_Tracking.Create\')")
    @PostMapping
    public ResponseEntity<AssetTrackingResponse> create(@Valid @RequestBody AssetTrackingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    /**
     * Delete a tracking event by id.
     */
    @PreAuthorize("hasAuthority(\'Asset.Asset_Tracking.Delete\')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
