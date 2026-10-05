package com.example.assets_management.asset_tracking.dto.request;
import com.example.assets_management.asset_tracking.entity.AssetTracking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Request payload for creating a new {@code AssetTracking} event.
 *
 * @param assetId   the asset being tracked (required)
 * @param action    action performed (required), e.g. MOVED, CHECKED_OUT
 * @param user      who performed the action
 * @param location  location at the time of the event
 * @param type      event category, e.g. MOVEMENT, ASSIGNMENT, MAINTENANCE
 * @param notes     optional free-form notes
 * @param eventTime when the event occurred (defaults to now if omitted)
 */
public record AssetTrackingRequest(
        @NotNull(message = "assetId is required")
        UUID assetId,

        @NotBlank(message = "action is required")
        @jakarta.validation.constraints.Size(max = 60, message = "action must be at most 60 characters")
        String action,

        @jakarta.validation.constraints.Size(max = 150, message = "user must be at most 150 characters")
        String user,

        @jakarta.validation.constraints.Size(max = 150, message = "location must be at most 150 characters")
        String location,

        @jakarta.validation.constraints.Size(max = 50, message = "type must be at most 50 characters")
        String type,

        String notes,

        LocalDateTime eventTime
) {
}
