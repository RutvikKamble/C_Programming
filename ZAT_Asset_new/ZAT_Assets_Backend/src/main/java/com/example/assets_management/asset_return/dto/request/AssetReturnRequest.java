package com.example.assets_management.asset_return.dto.request;
import com.example.assets_management.asset_return.entity.AssetReturn;

import com.example.assets_management.asset_return.enums.ReturnReason;
import com.example.assets_management.asset_return.enums.ReturnStatus;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for creating or updating an AssetReturn.
 */
public record AssetReturnRequest(
        UUID allocationId,
        UUID requestedBy,
        ReturnReason reason,
        ReturnStatus status,
        UUID verifiedBy,
        LocalDate verifiedOn
) {
}
