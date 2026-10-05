package com.example.assets_management.asset_tracking.repository;
import com.example.assets_management.asset_tracking.entity.AssetTracking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link AssetTracking}.
 */
public interface AssetTrackingRepository extends JpaRepository<AssetTracking, UUID> {

    /**
     * Find all tracking events for an asset, newest first.
     */
    List<AssetTracking> findByAssetIdOrderByEventTimeDesc(UUID assetId);

    /**
     * Fetch all tracking events, newest first, optionally filtered by type (paginated).
     */
    @Query("""
            SELECT t FROM AssetTracking t
            WHERE (:type IS NULL OR t.type = :type)
            ORDER BY t.eventTime DESC
            """)
    Page<AssetTracking> findAllByType(@Param("type") String type, Pageable pageable);

    /**
     * Fetch all tracking events, newest first, optionally filtered by type (non-paginated).
     */
    @Query("""
            SELECT t FROM AssetTracking t
            WHERE (:type IS NULL OR t.type = :type)
            ORDER BY t.eventTime DESC
            """)
    List<AssetTracking> findAllByType(@Param("type") String type);

    /** Count of tracking events for an asset. */
    long countByAssetId(UUID assetId);
}
