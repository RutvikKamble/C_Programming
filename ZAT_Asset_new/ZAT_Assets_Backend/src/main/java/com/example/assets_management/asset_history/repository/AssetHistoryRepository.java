package com.example.assets_management.asset_history.repository;
import com.example.assets_management.asset_history.entity.AssetHistory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link AssetHistory}.
 *
 * <p>Provides queries to fetch history for a single asset or across all
 * assets, with optional action filtering and pagination.</p>
 */
public interface AssetHistoryRepository extends JpaRepository<AssetHistory, UUID> {

    /**
     * Find all history entries for an asset, newest first.
     *
     * @param assetId the asset id
     * @return list of history entries
     */
    List<AssetHistory> findByAssetIdOrderByCreatedAtDesc(UUID assetId);

    /**
     * Fetch all history entries, newest first, optionally filtered by action (paginated).
     *
     * @param action   optional action filter (nullable for all)
     * @param pageable pagination metadata
     * @return a page of history entries
     */
    @Query("""
            SELECT h FROM AssetHistory h
            WHERE (:action IS NULL OR h.action = :action)
            ORDER BY h.createdAt DESC
            """)
    Page<AssetHistory> findAll(@Param("action") String action, Pageable pageable);

    /**
     * Fetch all history entries, newest first, optionally filtered by action (non-paginated).
     *
     * @param action optional action filter (nullable for all)
     * @return list of history entries
     */
    @Query("""
            SELECT h FROM AssetHistory h
            WHERE (:action IS NULL OR h.action = :action)
            ORDER BY h.createdAt DESC
            """)
    List<AssetHistory> findAll(@Param("action") String action);

    /** Count of history entries belonging to an asset. */
    long countByAssetId(UUID assetId);
}
