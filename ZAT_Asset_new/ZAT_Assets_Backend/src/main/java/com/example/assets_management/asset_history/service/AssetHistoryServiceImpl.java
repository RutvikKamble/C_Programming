package com.example.assets_management.asset_history.service;

import com.example.assets_management.asset_history.dto.request.AssetHistoryRequest;
import com.example.assets_management.asset_history.dto.response.AssetHistoryResponse;
import com.example.assets_management.asset_history.entity.AssetHistory;
import com.example.assets_management.asset_history.enums.AssetHistoryEventType;
import com.example.assets_management.asset_history.repository.AssetHistoryRepository;
import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.asset_master.repository.AssetRepository;
import com.example.assets_management.exception.ResourceNotFoundException;
import com.example.assets_management.security.scope.OrganizationScopeService;
import com.example.assets_management.security.context.CurrentUserService;
import org.springframework.security.access.AccessDeniedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Default implementation of AssetHistoryService.
 *
 * Asset History is treated as an append-only lifecycle trail.
 *
 * The legacy create(AssetHistoryRequest) method is retained so that
 * existing modules continue to compile and work.
 *
 * All history creation ultimately goes through recordEvent(...).
 */
@Service
public class AssetHistoryServiceImpl implements AssetHistoryService {

    private static final Logger log =
            LoggerFactory.getLogger(AssetHistoryServiceImpl.class);

    private final AssetHistoryRepository historyRepository;
    private final AssetRepository assetRepository;
    private final OrganizationScopeService organizationScopeService;
    private final CurrentUserService currentUserService;

    public AssetHistoryServiceImpl(
            AssetHistoryRepository historyRepository,
            AssetRepository assetRepository,
            OrganizationScopeService organizationScopeService,
            CurrentUserService currentUserService) {

        this.historyRepository = historyRepository;
        this.assetRepository = assetRepository;
        this.organizationScopeService = organizationScopeService;
        this.currentUserService = currentUserService;
    }

    /**
     * Get history for one asset.
     */
    @Override
    @Transactional(readOnly = true)
    public List<AssetHistoryResponse> findHistoryByAssetId(UUID assetId) {

        if (assetId == null) {
            throw new IllegalArgumentException("assetId is required");
        }

        if (currentUserService.isAuthenticated()) {
            organizationScopeService.requireAssetAccess(assetId);
        }

        if (!assetRepository.existsById(assetId)) {
            throw new ResourceNotFoundException(
                    "Asset not found: " + assetId
            );
        }

        return historyRepository
                .findByAssetIdOrderByCreatedAtDesc(assetId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all history entries.
     *
     * Optional action filtering is supported.
     */
    @Override
    @Transactional(readOnly = true)
    public List<AssetHistoryResponse> findAll(String action) {

        String normalizedAction = null;

        if (action != null && !action.isBlank()) {
            normalizedAction = action.trim().toUpperCase();
        }

        List<AssetHistory> result =
                historyRepository.findAll(normalizedAction);

        if (currentUserService.isAuthenticated()) {
            result = result.stream()
                    .filter(h -> h.getAsset() != null && organizationScopeService.hasAssetAccess(h.getAsset()))
                    .toList();
        }

        log.debug(
                "Fetched {} asset history entries. action={}",
                result.size(),
                normalizedAction
        );

        return result.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Backward-compatible create method.
     *
     * Existing modules can continue using:
     *
     * historyService.create(
     *     new AssetHistoryRequest(...)
     * );
     *
     * This does NOT create a separate history implementation.
     * It delegates to the centralized recordEvent(...) method.
     */
    @Override
    @Transactional
    public AssetHistoryResponse create(AssetHistoryRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Asset history request is required"
            );
        }

        return recordEvent(
                request.assetId(),
                request.action(),
                request.fieldChanged(),
                request.oldValue(),
                request.newValue(),
                request.description(),
                request.changedBy()
        );
    }

    /**
     * Record a standardized event using AssetHistoryEventType.
     */
    @Override
    @Transactional
    public AssetHistoryResponse recordEvent(
            UUID assetId,
            AssetHistoryEventType eventType,
            String fieldChanged,
            String oldValue,
            String newValue,
            String description,
            String changedBy) {

        if (eventType == null) {
            throw new IllegalArgumentException(
                    "eventType is required for history event"
            );
        }

        return recordEvent(
                assetId,
                eventType.name(),
                fieldChanged,
                oldValue,
                newValue,
                description,
                changedBy
        );
    }

    /**
     * Record a custom/legacy event using String action.
     *
     * This is also the common persistence path for the legacy
     * create(AssetHistoryRequest) method.
     */
    @Override
    @Transactional
    public AssetHistoryResponse recordEvent(
            UUID assetId,
            String action,
            String fieldChanged,
            String oldValue,
            String newValue,
            String description,
            String changedBy) {

        if (assetId == null) {
            throw new IllegalArgumentException(
                    "assetId is required for history event"
            );
        }

        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException(
                    "action is required for history event"
            );
        }

        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Asset not found: " + assetId
                        )
                );

        return recordEvent(
                asset,
                action,
                fieldChanged,
                oldValue,
                newValue,
                description,
                changedBy
        );
    }

    /**
     * Record a standardized event when the Asset entity is already available.
     */
    @Override
    @Transactional
    public AssetHistoryResponse recordEvent(
            Asset asset,
            AssetHistoryEventType eventType,
            String fieldChanged,
            String oldValue,
            String newValue,
            String description,
            String changedBy) {

        if (eventType == null) {
            throw new IllegalArgumentException(
                    "eventType is required for history event"
            );
        }

        return recordEvent(
                asset,
                eventType.name(),
                fieldChanged,
                oldValue,
                newValue,
                description,
                changedBy
        );
    }

    /**
     * Internal common history persistence method.
     *
     * Every history creation path ends here.
     */
    private AssetHistoryResponse recordEvent(
            Asset asset,
            String action,
            String fieldChanged,
            String oldValue,
            String newValue,
            String description,
            String changedBy) {

        if (asset == null || asset.getId() == null) {
            throw new IllegalArgumentException(
                    "asset is required for history event"
            );
        }

        if (currentUserService.isAuthenticated()) {
            organizationScopeService.requireAssetAccess(asset.getId());
            changedBy = currentUserService.requireUserId().toString();
        }

        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException(
                    "action is required for history event"
            );
        }

        AssetHistory history = new AssetHistory();

        history.setAsset(asset);

        history.setAction(
                action.trim().toUpperCase()
        );

        history.setFieldChanged(fieldChanged);

        history.setOldValue(oldValue);

        history.setNewValue(newValue);

        history.setDescription(description);

        history.setChangedBy(
                changedBy == null || changedBy.isBlank()
                        ? "SYSTEM"
                        : changedBy.trim()
        );

        AssetHistory saved =
                historyRepository.save(history);

        log.info(
                "Recorded asset history event '{}' for asset '{}' ({})",
                saved.getAction(),
                asset.getAssetCode(),
                asset.getId()
        );

        return toResponse(saved);
    }

    /**
     * Convert entity to response DTO.
     */
    private AssetHistoryResponse toResponse(
            AssetHistory history) {

        return new AssetHistoryResponse(
                history.getId(),
                history.getAsset(),
                history.getAction(),
                history.getFieldChanged(),
                history.getOldValue(),
                history.getNewValue(),
                history.getDescription(),
                history.getChangedBy(),
                history.getCreatedAt()
        );
    }
}