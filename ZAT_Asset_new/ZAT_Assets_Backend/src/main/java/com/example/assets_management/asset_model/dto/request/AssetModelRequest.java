package com.example.assets_management.asset_model.dto.request;
import com.example.assets_management.asset_model.entity.AssetModel;

import com.example.assets_management.asset_category.enums.DepreciationMethod;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request payload for creating or updating an {@code AssetModel}.
 *
 * @param categoryId            id of the owning category (required)
 * @param name                  model name (required)
 * @param specSheet             optional JSON specification sheet
 * @param defaultWarrantyMonths default warranty in months
 * @param depreciationMethod    default depreciation method
 */
public record AssetModelRequest(
        @NotNull(message = "categoryId is required")
        UUID categoryId,

        @NotBlank(message = "name is required")
        @Size(max = 150, message = "name must be at most 150 characters")
        String name,

        String specSheet,

        @Min(value = 0, message = "defaultWarrantyMonths cannot be negative")
        Integer defaultWarrantyMonths,

        DepreciationMethod depreciationMethod
) {
}
