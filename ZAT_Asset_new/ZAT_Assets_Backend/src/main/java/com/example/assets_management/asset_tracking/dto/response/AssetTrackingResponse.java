package com.example.assets_management.asset_tracking.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response payload exposing a tracking event to API consumers.
 *
 * <p>Field names match the frontend {@code TrackingEvent} contract:
 * {@code id}, {@code assetId}, {@code action}, {@code user}, {@code location},
 * {@code timestamp}, {@code type}.</p>
 *
 * @param id        event id
 * @param assetId   the asset id
 * @param assetCode the asset code (convenience)
 * @param action    action performed
 * @param user      who performed the action
 * @param location  location at the time of the event
 * @param timestamp the event timestamp
 * @param type      event category
 * @param notes     optional free-form notes
 */
public record AssetTrackingResponse(
        UUID id,
        UUID assetId,
        String assetCode,
        String action,
        String user,
        String location,
        LocalDateTime timestamp,
        String type,
        String notes
) {
}
