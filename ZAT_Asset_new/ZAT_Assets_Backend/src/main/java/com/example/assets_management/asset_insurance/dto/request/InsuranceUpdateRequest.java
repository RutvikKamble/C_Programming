package com.example.assets_management.asset_insurance.dto.request;

import com.example.assets_management.asset_insurance.enums.InsuranceStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InsuranceUpdateRequest(
        @NotBlank(message = "provider is required")
        @Size(max = 150, message = "provider must be at most 150 characters")
        String provider,

        @NotBlank(message = "policyNumber is required")
        @Size(max = 100, message = "policyNumber must be at most 100 characters")
        String policyNumber,

        @Size(max = 5000, message = "coverageDetails must be at most 5000 characters")
        String coverageDetails,

        @DecimalMin(value = "0.00", message = "premium must be zero or greater")
        BigDecimal premium,

        LocalDate startDate,

        @NotNull(message = "expiryDate is required")
        LocalDate expiryDate,

        @NotNull(message = "status is required")
        InsuranceStatus status
) {}
