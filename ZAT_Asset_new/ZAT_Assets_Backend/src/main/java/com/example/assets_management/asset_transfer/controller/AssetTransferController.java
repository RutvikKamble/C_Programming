package com.example.assets_management.asset_transfer.controller;

import org.springframework.security.access.prepost.PreAuthorize;

import com.example.assets_management.asset_transfer.dto.TransferRequest;
import com.example.assets_management.asset_transfer.entity.AssetTransfer;
import com.example.assets_management.asset_transfer.service.AssetTransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetTransferController {

    private final AssetTransferService assetTransferService;

    /**
     * Transfers an asset to another branch, another employee, or both.
     * The current branch/employee are read from the Asset automatically.
     */
    @PreAuthorize("hasAuthority(\'Asset.Asset_Transfer.Transfer\')")
    @PostMapping("/{assetId}/transfer")
    public ResponseEntity<AssetTransfer> transfer(
            @PathVariable UUID assetId,
            @Valid @RequestBody TransferRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assetTransferService.transfer(assetId, request));
    }

    /** Returns the complete transfer history for an asset. */
    @PreAuthorize("hasAuthority(\'Asset.Asset_Transfer.View\')")
    @GetMapping("/{assetId}/transfers")
    public ResponseEntity<List<AssetTransfer>> getTransferHistory(
            @PathVariable UUID assetId) {
        return ResponseEntity.ok(assetTransferService.findByAssetId(assetId));
    }

    /** Returns one transfer record. */
    @PreAuthorize("hasAuthority(\'Asset.Asset_Transfer.View\')")
    @GetMapping("/transfers/{id}")
    public ResponseEntity<AssetTransfer> getTransfer(
            @PathVariable UUID id) {
        return ResponseEntity.ok(assetTransferService.findById(id));
    }

    /** Administrative deletion of a transfer record. */
    @PreAuthorize("hasAuthority(\'Asset.Asset_Transfer.Delete\')")
    @DeleteMapping("/transfers/{id}")
    public ResponseEntity<Void> deleteTransfer(
            @PathVariable UUID id) {
        assetTransferService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
