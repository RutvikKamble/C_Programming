package com.example.assets_management.asset_model.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.example.assets_management.asset_model.entity.AssetModel;
import com.example.assets_management.asset_model.service.AssetModelService;

import com.example.assets_management.asset_model.dto.request.AssetModelRequest;
import com.example.assets_management.asset_model.dto.response.AssetModelResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller exposing CRUD and list operations for {@link AssetModel}.
 *
 * <p>Base path: {@code /api/asset-models}. Supports optional
 * filtering by {@code categoryId} and {@code name}.</p>
 *
 * <ul>
 *   <li>GET {@code /api/asset-models} ÃƒÂ¢Ã¢â€šÂ¬Ã¢â‚¬Â list, optional {@code categoryId}/{@code name}</li>
 *   <li>GET {@code /api/asset-models/{id}} ÃƒÂ¢Ã¢â€šÂ¬Ã¢â‚¬Â single model</li>
 *   <li>POST {@code /api/asset-models} ÃƒÂ¢Ã¢â€šÂ¬Ã¢â‚¬Â create</li>
 *   <li>PUT {@code /api/asset-models/{id}} ÃƒÂ¢Ã¢â€šÂ¬Ã¢â‚¬Â update</li>
 *   <li>DELETE {@code /api/asset-models/{id}} ÃƒÂ¢Ã¢â€šÂ¬Ã¢â‚¬Â delete</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/asset-models")
@CrossOrigin(origins = "http://localhost:5173")
public class AssetModelController {

    private final AssetModelService service;

    public AssetModelController(AssetModelService service) {
        this.service = service;
    }

    /**
     * List models with optional category and name filters.
     *
     * @param categoryId optional category filter
     * @param name       optional name search
     * @return a list of model responses
     */
    @PreAuthorize("hasAuthority(\'Asset.Asset_Model.View\')")
    @GetMapping
    public ResponseEntity<List<AssetModelResponse>> getAllModels(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String name) {

        return ResponseEntity.ok(service.findAll(categoryId, name));
    }

    /**
     * Fetch a single model by id.
     *
     * @param id model id
     * @return the matching model
     */
    @PreAuthorize("hasAuthority(\'Asset.Asset_Model.View\')")
    @GetMapping("/{id}")
    public ResponseEntity<AssetModelResponse> getModelById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    /**
     * Create a new model.
     *
     * @param request validated creation payload
     * @return created model with 201 status
     */
    @PreAuthorize("hasAuthority(\'Asset.Asset_Model.Create\')")
    @PostMapping
    public ResponseEntity<AssetModelResponse> createModel(@Valid @RequestBody AssetModelRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    /**
     * Update an existing model.
     *
     * @param id      model id
     * @param request validated update payload
     * @return updated model
     */
    @PreAuthorize("hasAuthority(\'Asset.Asset_Model.Update\')")
    @PutMapping("/{id}")
    public ResponseEntity<AssetModelResponse> updateModel(
            @PathVariable UUID id,
            @Valid @RequestBody AssetModelRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    /**
     * Delete a model by id.
     *
     * @param id model id
     * @return 204 No Content
     */
    @PreAuthorize("hasAuthority(\'Asset.Asset_Model.Delete\')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModel(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
