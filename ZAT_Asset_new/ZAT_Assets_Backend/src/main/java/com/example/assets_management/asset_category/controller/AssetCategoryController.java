package com.example.assets_management.asset_category.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.example.assets_management.asset_category.dto.request.BulkDeleteRequest;
import com.example.assets_management.asset_category.dto.request.BulkUpdateRequest;
import com.example.assets_management.asset_category.entity.AssetCategory;
import com.example.assets_management.asset_category.service.AssetCategoryService;

import com.example.assets_management.asset_category.dto.request.AssetCategoryRequest;
import com.example.assets_management.asset_category.dto.request.AssetCategoryRequest;
import com.example.assets_management.asset_category.dto.request.BulkDeleteRequest;
import com.example.assets_management.asset_category.dto.request.BulkUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/asset-categories")
@CrossOrigin(origins = "http://localhost:5173")
public class AssetCategoryController {

    private final AssetCategoryService service;

    public AssetCategoryController(AssetCategoryService service) {
        this.service = service;
    }

    // ---------- Existing ----------
    @PreAuthorize("hasAuthority(\'Asset.Asset_Category.Create\')")
    @PostMapping
    public ResponseEntity<String> saveCategory(@RequestBody AssetCategoryRequest request) {
        service.saveCategory(request);
        return ResponseEntity.ok("Category Saved Successfully");
    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Category.Update\')")
    @PutMapping("/{id}")
    public ResponseEntity<AssetCategory> updateCategory(
            @PathVariable UUID id,
            @RequestBody AssetCategory category) {
        return new ResponseEntity<>(
                service.updateCategory(id, category),
                HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Category.Delete\')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        service.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Category.View\')")
    @GetMapping("/{id}")
    public ResponseEntity<AssetCategory> getCategory(@PathVariable UUID id) {
        return new ResponseEntity<>(
                service.getCategoryById(id),
                HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Category.View\')")
    @GetMapping
    public ResponseEntity<List<AssetCategory>> getAllCategories() {
        return new ResponseEntity<>(
                service.getAllCategories(),
                HttpStatus.OK);
    }

    // ---------- New ----------
    @PreAuthorize("hasAuthority(\'Asset.Asset_Category.Delete\')")
    @DeleteMapping("/bulk")
    public ResponseEntity<Void> bulkDelete(@RequestBody BulkDeleteRequest request) {
        service.bulkDelete(request.getIds());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Category.Update\')")
    @PutMapping("/bulk")
    public ResponseEntity<List<AssetCategory>> bulkUpdate(@RequestBody BulkUpdateRequest request) {
        return new ResponseEntity<>(service.bulkUpdate(request.getCategories()), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Category.Import\')")
    @PostMapping("/import")
    public ResponseEntity<String> importCategories(@RequestParam("file") MultipartFile file) throws IOException {
        service.importCategories(file);
        return ResponseEntity.ok("Import successful");
    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Category.Export\')")
    @GetMapping("/export")
    public void exportCategories(HttpServletResponse response) throws IOException {
        service.exportCategories(response);
    }

    @PreAuthorize("hasAuthority(\'Asset.Asset_Category.Update\')")
    @PatchMapping("/{id}")
    public ResponseEntity<AssetCategory> patchCategory(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> updates) {
        return new ResponseEntity<>(service.patchCategory(id, updates), HttpStatus.OK);
    }
}
