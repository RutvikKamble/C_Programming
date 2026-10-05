package com.example.assets_management.asset_category.repository;
import com.example.assets_management.asset_category.entity.AssetCategory;

import com.example.assets_management.asset_category.entity.AssetCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface AssetCategoryRepository extends JpaRepository<AssetCategory, UUID> {

    List<AssetCategory> findByParentCategoryId(UUID parentId);

    @Modifying
    @Transactional
    @Query("UPDATE AssetCategory c SET c.parentCategory = NULL WHERE c.parentCategory.id IN :ids")
    void detachChildren(@Param("ids") List<UUID> ids);
}
