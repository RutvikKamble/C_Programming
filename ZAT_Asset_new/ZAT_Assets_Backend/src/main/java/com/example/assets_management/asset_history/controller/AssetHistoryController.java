package com.example.assets_management.asset_history.controller;

import com.example.assets_management.asset_history.dto.response.AssetHistoryResponse;
import com.example.assets_management.asset_history.service.AssetHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/** Read-only API for the immutable asset lifecycle timeline. */
@RestController
@RequestMapping("/api/asset-history")
public class AssetHistoryController {

    private final AssetHistoryService service;

    public AssetHistoryController(AssetHistoryService service) {
        this.service = service;
    }

    @PreAuthorize("hasAuthority('Asset.Asset_History.View')")
    @GetMapping
    public ResponseEntity<List<AssetHistoryResponse>> getAll(
            @RequestParam(required = false) String action) {
        return ResponseEntity.ok(service.findAll(action));
    }

    @PreAuthorize("hasAuthority('Asset.Asset_History.View')")
    @GetMapping("/asset/{assetId}")
    public ResponseEntity<List<AssetHistoryResponse>> getByAssetId(
            @PathVariable UUID assetId) {
        return ResponseEntity.ok(service.findHistoryByAssetId(assetId));
    }
}