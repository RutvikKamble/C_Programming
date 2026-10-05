package com.example.assets_management.asset_model.dto.response;
import com.example.assets_management.asset_model.entity.AssetModel;

import com.example.assets_management.asset_category.enums.DepreciationMethod;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response payload exposing an {@code AssetModel} to API consumers.
 *
 * @param id                    model id
 * @param categoryId            owning category id
 * @param categoryName          owning category name
 * @param name                  model name
 * @param specSheet             specification sheet (JSON)
 * @param defaultWarrantyMonths default warranty in months
 * @param depreciationMethod    default depreciation method
 * @param createdAt             created timestamp
 */
public record AssetModelResponse(
        UUID id,
        UUID categoryId,
        String categoryName,
        String name,
        String specSheet,
        Integer defaultWarrantyMonths,
        DepreciationMethod depreciationMethod,
        LocalDateTime createdAt
) {
}
