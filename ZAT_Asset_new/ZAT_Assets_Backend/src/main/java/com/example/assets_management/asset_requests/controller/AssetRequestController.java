package com.example.assets_management.asset_requests.controller;

import com.example.assets_management.asset_requests.entity.AssetRequest;
import com.example.assets_management.asset_requests.service.AssetRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/asset-requests")
public class AssetRequestController {

    private final AssetRequestService requestService;

    public AssetRequestController(AssetRequestService requestService) {
        this.requestService = requestService;
    }

    @PreAuthorize("hasAuthority('Asset.Asset_Request.Create')")
    @PostMapping
    public ResponseEntity<AssetRequest> createRequest(
            @Valid @RequestBody AssetRequest request) {

        return new ResponseEntity<>(
                requestService.createRequest(request),
                HttpStatus.CREATED
        );
    }

    @PreAuthorize("hasAuthority('Asset.Asset_Request.Update')")
    @PutMapping("/{id}")
    public ResponseEntity<AssetRequest> updateRequest(
            @PathVariable Long id,
            @Valid @RequestBody AssetRequest request) {

        return ResponseEntity.ok(
                requestService.updateRequest(id, request)
        );
    }

    @PreAuthorize("hasAuthority('Asset.Asset_Request.Delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(
            @PathVariable Long id) {

        requestService.deleteRequest(id);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('Asset.Asset_Request.View')")
    @GetMapping("/{id}")
    public ResponseEntity<AssetRequest> getRequestById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                requestService.getRequestById(id)
        );
    }

    @PreAuthorize("hasAuthority('Asset.Asset_Request.View')")
    @GetMapping
    public ResponseEntity<List<AssetRequest>> getAllRequests() {

        return ResponseEntity.ok(
                requestService.getAllRequests()
        );
    }

    @PreAuthorize("hasAuthority('Asset.Asset_Request.View')")
    @GetMapping("/asset/{assetId}")
    public ResponseEntity<List<AssetRequest>> getRequestsByAssetId(
            @PathVariable UUID assetId) {

        return ResponseEntity.ok(
                requestService.getRequestsByAssetId(assetId)
        );
    }

    @PreAuthorize("hasAuthority('Asset.Asset_Request.View')")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AssetRequest>> getRequestsByStatus(
            @PathVariable String status) {

        return ResponseEntity.ok(
                requestService.getRequestsByStatus(status)
        );
    }

    // New workflow endpoint
    @PreAuthorize("hasAuthority('Asset.Asset_Request.Send')")
    @PatchMapping("/{id}/send")
    public ResponseEntity<AssetRequest> send(
            @PathVariable Long id,
            @RequestParam String by) {

        return ResponseEntity.ok(
                requestService.send(id, by)
        );
    }

    // New workflow endpoint
    @PreAuthorize("hasAuthority('Asset.Asset_Request.Receive')")
    @PatchMapping("/{id}/receive")
    public ResponseEntity<AssetRequest> receive(
            @PathVariable Long id,
            @RequestParam String by) {

        return ResponseEntity.ok(
                requestService.receive(id, by)
        );
    }

    // New workflow endpoint
    @PreAuthorize("hasAuthority('Asset.Asset_Request.Approve_Manager')")
    @PatchMapping("/{id}/approve")
    public ResponseEntity<AssetRequest> approve(
            @PathVariable Long id,
            @RequestParam String by) {

        return ResponseEntity.ok(
                requestService.approve(id, by)
        );
    }

    // New workflow endpoint
    @PreAuthorize("hasAuthority('Asset.Asset_Request.Create_Asset')")
    @PatchMapping("/{id}/create-asset")
    public ResponseEntity<AssetRequest> createAsset(
            @PathVariable Long id,
            @RequestParam String by) {

        return ResponseEntity.ok(
                requestService.createAsset(id, by)
        );
    }

    // New workflow endpoint
    @PreAuthorize("hasAuthority('Asset.Asset_Request.Pay_Bill')")
    @PatchMapping("/{id}/pay-bill")
    public ResponseEntity<AssetRequest> payBill(
            @PathVariable Long id,
            @RequestParam String by) {

        return ResponseEntity.ok(
                requestService.payBill(id, by)
        );
    }

    // New workflow endpoint
    @PreAuthorize("hasAuthority('Asset.Asset_Request.Transfer')")
    @PatchMapping("/{id}/transfer")
    public ResponseEntity<AssetRequest> transfer(
            @PathVariable Long id,
            @RequestParam String by) {

        return ResponseEntity.ok(
                requestService.transfer(id, by)
        );
    }

    // New workflow endpoint
    @PreAuthorize("hasAuthority('Asset.Asset_Request.Complete')")
    @PatchMapping("/{id}/complete")
    public ResponseEntity<AssetRequest> complete(
            @PathVariable Long id,
            @RequestParam String by) {

        return ResponseEntity.ok(
                requestService.complete(id, by)
        );
    }

    // New workflow endpoint
    @PreAuthorize("hasAuthority('Asset.Asset_Request.Cancel')")
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<AssetRequest> cancel(
            @PathVariable Long id,
            @RequestParam String by,
            @RequestParam(required = false) String reason) {

        return ResponseEntity.ok(
                requestService.cancel(id, by, reason)
        );
    }

    // Existing employee request workflow retained
    @PreAuthorize("hasAuthority('Asset.Asset_Request.Approve_Manager')")
    @PatchMapping("/{id}/approve-dept-head")
    public ResponseEntity<AssetRequest> approveByDeptHead(
            @PathVariable Long id,
            @RequestParam String approvedBy) {

        return ResponseEntity.ok(
                requestService.approveByDeptHead(id, approvedBy)
        );
    }

    @PreAuthorize("hasAuthority('Asset.Asset_Request.Approve_Manager')")
    @PatchMapping("/{id}/approve-asset-mgr")
    public ResponseEntity<AssetRequest> approveByAssetManager(
            @PathVariable Long id,
            @RequestParam String approvedBy) {

        return ResponseEntity.ok(
                requestService.approveByAssetManager(id, approvedBy)
        );
    }

    @PreAuthorize("hasAuthority('Asset.Asset_Request.Fulfill')")
    @PostMapping("/{id}/fulfill")
    public ResponseEntity<AssetRequest> fulfill(
            @PathVariable Long id,
            @RequestParam UUID assetId) {

        return ResponseEntity.ok(
                requestService.fulfill(id, assetId)
        );
    }

    @PreAuthorize("hasAuthority('Asset.Asset_Request.Reject')")
    @PatchMapping("/{id}/reject")
    public ResponseEntity<AssetRequest> rejectRequest(
            @PathVariable Long id,
            @RequestParam String rejectedBy,
            @RequestParam(required = false) String reason) {

        return ResponseEntity.ok(
                requestService.rejectRequest(id, rejectedBy, reason)
        );
    }
}