package com.example.assets_management.asset_return.dto.response;
import com.example.assets_management.asset_return.entity.AssetReturn;

import com.example.assets_management.asset_return.enums.ReturnReason;
import com.example.assets_management.asset_return.enums.ReturnStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for AssetReturn response.
 */
public record AssetReturnResponse(
        UUID id,
        UUID allocationId,
        String assetCode,
        String assetName,
        UUID requestedBy,
        String requestedByName,
        ReturnReason reason,
        ReturnStatus status,
        UUID verifiedBy,
        String verifiedByName,
        LocalDate verifiedOn,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
