package com.example.assets_management.asset_disposal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/** Request used to raise a disposal review for an asset. */
public record DisposalReviewRequest(
        @NotNull(message = "reviewedById is required")
        UUID reviewedById,

        @NotBlank(message = "reason is required")
        @Pattern(
                regexp = "(?i)END_OF_LIFE|DAMAGED|OBSOLETE|SOLD",
                message = "reason must be one of END_OF_LIFE, DAMAGED, OBSOLETE, SOLD"
        )
        String reason,

        @Size(max = 2000, message = "remarks must be at most 2000 characters")
        String remarks
) {
}
