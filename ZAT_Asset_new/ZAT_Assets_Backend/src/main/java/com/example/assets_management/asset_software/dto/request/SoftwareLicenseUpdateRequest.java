package com.example.assets_management.asset_software.dto.request;

import com.example.assets_management.asset_software.enums.SoftwareLicenseStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record SoftwareLicenseUpdateRequest(
        @Size(max = 150) String name,
        @Size(max = 100) String publisher,
        @Size(max = 100) String manufacturer,
        @Size(max = 100) String licenseCategory,
        UUID supplierId,
        String licenseKey,
        @Min(1) Integer seatCount,
        @FutureOrPresent LocalDate expiryDate,
        LocalDate purchaseDate,
        @DecimalMin(value = "0.00") BigDecimal purchaseCost,
        @DecimalMin(value = "0.00") BigDecimal renewalCost,
        @Size(max = 100) String orderNumber,
        @Size(max = 100) String purchaseOrder,
        @Size(max = 150) String licenseContactName,
        @Email @Size(max = 150) String licenseContactEmail,
        @Size(max = 2000) String notes,
        Boolean reassignable,
        Boolean maintained,
        LocalDate terminationDate,
        SoftwareLicenseStatus status,
        @Size(max = 150) String changedBy
) {}
