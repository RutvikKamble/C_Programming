package com.example.assets_management.asset_disposal.controller;

import com.example.assets_management.asset_disposal.dto.request.DisposalFinalizeRequest;
import com.example.assets_management.asset_disposal.dto.request.DisposalReviewRequest;
import com.example.assets_management.asset_disposal.dto.response.DisposalResponse;
import com.example.assets_management.asset_disposal.enums.DisposalStatus;
import com.example.assets_management.asset_disposal.service.AssetDisposalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AssetDisposalController {

    private final AssetDisposalService disposalService;

    /**
     * Starts the disposal workflow for an active asset.
     * POST /api/assets/{assetId}/disposal/review
     */
    @PreAuthorize("hasAuthority('Asset.Disposal.Initiate')")
    @PostMapping("/assets/{assetId}/disposal/review")
    public ResponseEntity<DisposalResponse> createReview(
            @PathVariable UUID assetId,
            @Valid @RequestBody DisposalReviewRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(disposalService.createReview(assetId, request));
    }

    /**
     * Approves a pending disposal.
     * POST /api/disposal/{id}/approve?approvedById={employeeId}
     */
    @PreAuthorize("hasAuthority('Asset.Disposal.Approve')")
    @PostMapping("/disposal/{id}/approve")
    public ResponseEntity<DisposalResponse> approve(
            @PathVariable UUID id,
            @RequestParam UUID approvedById,
            @RequestParam(required = false) String remarks) {

        return ResponseEntity.ok(disposalService.approve(id, approvedById, remarks));
    }

    /**
     * Rejects a pending disposal.
     * POST /api/disposal/{id}/reject?rejectedById={employeeId}&rejectionReason=...
     */
    @PreAuthorize("hasAuthority('Asset.Disposal.Approve')")
    @PostMapping("/disposal/{id}/reject")
    public ResponseEntity<DisposalResponse> reject(
            @PathVariable UUID id,
            @RequestParam UUID rejectedById,
            @RequestParam String rejectionReason) {

        return ResponseEntity.ok(disposalService.reject(id, rejectedById, rejectionReason));
    }

    /**
     * Records the physical disposal and permanently closes the asset.
     * POST /api/disposal/{id}/finalize
     */
    @PreAuthorize("hasAuthority('Asset.Disposal.Complete')")
    @PostMapping("/disposal/{id}/finalize")
    public ResponseEntity<DisposalResponse> finalizeDisposal(
            @PathVariable UUID id,
            @Valid @RequestBody DisposalFinalizeRequest request) {

        return ResponseEntity.ok(disposalService.finalizeDisposal(id, request));
    }

    @PreAuthorize("hasAuthority('Asset.Disposal.View')")
    @GetMapping("/disposal/{id}")
    public ResponseEntity<DisposalResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(disposalService.findById(id));
    }

    @PreAuthorize("hasAuthority('Asset.Disposal.View')")
    @GetMapping("/disposals")
    public ResponseEntity<List<DisposalResponse>> getAll(
            @RequestParam(required = false) DisposalStatus status) {

        return ResponseEntity.ok(disposalService.findAll(status));
    }

    @PreAuthorize("hasAuthority('Asset.Disposal.View')")
    @GetMapping("/assets/{assetId}/disposals")
    public ResponseEntity<List<DisposalResponse>> getByAssetId(@PathVariable UUID assetId) {
        return ResponseEntity.ok(disposalService.findByAssetId(assetId));
    }
}
