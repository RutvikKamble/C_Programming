package com.example.assets_management.asset_history.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Request payload for creating an Asset History entry.
 *
 * This DTO is retained for backward compatibility with existing modules
 * that still use:
 *
 *     historyService.create(new AssetHistoryRequest(...))
 *
 * Internally, AssetHistoryService delegates this request to the
 * centralized recordEvent(...) method.
 */
public record AssetHistoryRequest(

        @NotNull(message = "assetId is required")
        UUID assetId,

        @NotBlank(message = "action is required")
        @Size(max = 50, message = "action must be at most 50 characters")
        String action,

        @Size(max = 100, message = "fieldChanged must be at most 100 characters")
        String fieldChanged,

        String oldValue,

        String newValue,

        String description,

        @Size(max = 150, message = "changedBy must be at most 150 characters")
        String changedBy

) {
}