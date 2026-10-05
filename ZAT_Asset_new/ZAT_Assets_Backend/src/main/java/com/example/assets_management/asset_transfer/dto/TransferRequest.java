package com.example.assets_management.asset_transfer.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Request for moving an asset to another branch, another employee, or both.
 *
 * <p>The source branch/employee are deliberately not accepted from the client;
 * they are captured from the current Asset state so the transfer record always
 * reflects the real state immediately before the operation.</p>
 */
public record TransferRequest(
        UUID toBranchId,
        UUID toEmployeeId,
        @NotNull(message = "transferDate is required")
        LocalDate transferDate,
        @Size(max = 1000, message = "remarks must be at most 1000 characters")
        String remarks
) {
}
