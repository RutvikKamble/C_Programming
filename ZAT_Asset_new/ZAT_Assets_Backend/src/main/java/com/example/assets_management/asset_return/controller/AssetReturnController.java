package com.example.assets_management.asset_return.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.example.assets_management.asset_return.entity.AssetReturn;
import com.example.assets_management.asset_return.service.AssetReturnService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/asset-returns")
@RequiredArgsConstructor
public class AssetReturnController {
    private final AssetReturnService assetReturnService;

    @PreAuthorize("hasAuthority(\'Asset.Asset_Return.View\')")
    @GetMapping
    public List<AssetReturn> getAll() {
        return assetReturnService.findAll();
    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Return.View\')")
    @GetMapping("/{id}")
    public ResponseEntity<AssetReturn> getById(@PathVariable UUID id) {
        return assetReturnService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Return.Create\')")
    @PostMapping
    public AssetReturn save(@RequestBody AssetReturn assetReturn) {
        return assetReturnService.save(assetReturn);
    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Return.Update\')")
    @PutMapping("/{id}")
    public ResponseEntity<AssetReturn> update(@PathVariable UUID id, @RequestBody AssetReturn assetReturn) {
        return ResponseEntity.ok(assetReturnService.update(id, assetReturn));
    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Return.Return_Verify\')")
    @PostMapping("/{id}/verify")
    public ResponseEntity<AssetReturn> verify(@PathVariable UUID id, @RequestParam UUID verifierId) {
        return ResponseEntity.ok(assetReturnService.verify(id, verifierId));
    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Return.Complete\')")
    @PostMapping("/{id}/complete")
    public ResponseEntity<AssetReturn> complete(@PathVariable UUID id) {
        return ResponseEntity.ok(assetReturnService.complete(id));
    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Return.Delete\')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        assetReturnService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
