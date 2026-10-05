package com.example.assets_management.asset_tracking.service;
import com.example.assets_management.asset_tracking.entity.AssetTracking;
import com.example.assets_management.asset_tracking.repository.AssetTrackingRepository;

import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.asset_master.repository.AssetRepository;
import com.example.assets_management.asset_tracking.dto.request.AssetTrackingRequest;
import com.example.assets_management.asset_tracking.dto.response.AssetTrackingResponse;
import com.example.assets_management.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link AssetTrackingService}.
 */
@Service
public class AssetTrackingServiceImpl implements AssetTrackingService {

    private static final Logger log = LoggerFactory.getLogger(AssetTrackingServiceImpl.class);

    private final AssetTrackingRepository trackingRepository;
    private final AssetRepository assetRepository;

    public AssetTrackingServiceImpl(AssetTrackingRepository trackingRepository,
                                    AssetRepository assetRepository) {
        this.trackingRepository = trackingRepository;
        this.assetRepository = assetRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetTrackingResponse> findByAssetId(UUID assetId) {
        return trackingRepository.findByAssetIdOrderByEventTimeDesc(assetId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetTrackingResponse> findAll(String type) {
        List<AssetTracking> result = trackingRepository.findAllByType(type);
        log.debug("Fetched {} tracking events (type={})", result.size(), type);
        return result.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AssetTrackingResponse create(AssetTrackingRequest request) {
        Asset asset = assetRepository.findById(request.assetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found: " + request.assetId()));

        AssetTracking tracking = new AssetTracking();
        tracking.setAsset(asset);
        tracking.setAction(request.action().trim());
        tracking.setUser(request.user());
        tracking.setLocation(request.location());
        tracking.setType(request.type());
        tracking.setNotes(request.notes());
        tracking.setEventTime(request.eventTime() != null ? request.eventTime() : LocalDateTime.now());

        AssetTracking saved = trackingRepository.save(tracking);
        log.info("Recorded '{}' tracking event for asset {} (id {})",
                saved.getAction(), asset.getAssetCode(), saved.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        AssetTracking tracking = trackingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tracking event not found: " + id));
        trackingRepository.delete(tracking);
        log.info("Deleted tracking event with id {}", id);
    }

    private AssetTrackingResponse toResponse(AssetTracking tracking) {
        Asset asset = tracking.getAsset();
        return new AssetTrackingResponse(
                tracking.getId(),
                asset != null ? asset.getId() : null,
                asset != null ? asset.getAssetCode() : null,
                tracking.getAction(),
                tracking.getUser(),
                tracking.getLocation(),
                tracking.getEventTime() != null ? tracking.getEventTime() : tracking.getCreatedAt(),
                tracking.getType(),
                tracking.getNotes()
        );
    }
}
