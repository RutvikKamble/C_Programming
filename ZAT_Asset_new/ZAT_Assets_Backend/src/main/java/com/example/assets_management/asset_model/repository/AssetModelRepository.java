package com.example.assets_management.asset_model.repository;
import com.example.assets_management.asset_model.entity.AssetModel;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link AssetModel}.
 *
 * <p>Provides pagination, name search and category based filtering used by the
 * list endpoint. Additional indexes are recommended on {@code category_id} and
 * {@code name} for the queries below.</p>
 */
public interface AssetModelRepository extends JpaRepository<AssetModel, UUID> {

    /**
     * Find all models for a given category id, paginated.
     *
     * @param categoryId the category id (nullable)
     * @param pageable   pagination metadata
     * @return a page of matching models
     */
    @Query("""
            SELECT m FROM AssetModel m
            WHERE (:categoryId IS NULL OR m.category.id = :categoryId)
            """)
    Page<AssetModel> findByCategoryId(@Param("categoryId") UUID categoryId, Pageable pageable);

    /**
     * Find all models for a given category id (non-paginated).
     *
     * @param categoryId the category id (nullable)
     * @return a list of matching models
     */
    @Query("""
            SELECT m FROM AssetModel m
            WHERE (:categoryId IS NULL OR m.category.id = :categoryId)
            """)
    List<AssetModel> findByCategoryId(@Param("categoryId") UUID categoryId);

    /**
     * Find all models matching a partial name (case-insensitive), paginated.
     *
     * @param name     the search string
     * @param pageable pagination metadata
     * @return a page of matching models
     */
    Page<AssetModel> findByNameContainingIgnoreCase(String name, Pageable pageable);

    /**
     * Find all models matching a partial name (case-insensitive), non-paginated.
     *
     * @param name the search string
     * @return a list of matching models
     */
    List<AssetModel> findByNameContainingIgnoreCase(String name);

    /** Count of models belonging to a category. */
    long countByCategoryId(UUID categoryId);
}
