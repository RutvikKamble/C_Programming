package com.example.assets_management.asset_disposal.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** Request used to record the physical disposal and close the asset. */
public record DisposalFinalizeRequest(
        @NotNull(message = "finalizedById is required")
        UUID finalizedById,

        @NotNull(message = "disposalDate is required")
        LocalDate disposalDate,

        @DecimalMin(value = "0.00", message = "disposalValue must be zero or greater")
        BigDecimal disposalValue,

        @NotNull(message = "disposalMethod is required")
        @Pattern(
                regexp = "(?i)SALE|SCRAP|RECYCLE|DONATION|RETURN_TO_VENDOR|TRADE_IN|OTHER",
                message = "disposalMethod must be one of SALE, SCRAP, RECYCLE, DONATION, RETURN_TO_VENDOR, TRADE_IN, OTHER"
        )
        String disposalMethod,

        @Size(max = 2000, message = "remarks must be at most 2000 characters")
        String remarks
) {
}
