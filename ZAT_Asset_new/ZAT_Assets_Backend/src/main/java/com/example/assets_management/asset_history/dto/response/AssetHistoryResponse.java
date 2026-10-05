package com.example.assets_management.asset_history.dto.response;
import com.example.assets_management.asset_history.entity.AssetHistory;

import com.example.assets_management.asset_master.entity.Asset;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response payload exposing an {@code AssetHistory} entry to API consumers.
 *
 * @param id           history entry id
 * @param asset        the asset this entry belongs to
 * @param action       high level action
 * @param fieldChanged the field that changed
 * @param oldValue     previous value
 * @param newValue     new value
 * @param description  natural language description
 * @param changedBy    who performed the change
 * @param createdAt    recorded timestamp
 */
public record AssetHistoryResponse(
        UUID id,
        Asset asset,
        String action,
        String fieldChanged,
        String oldValue,
        String newValue,
        String description,
        String changedBy,
        LocalDateTime createdAt
) {
}

