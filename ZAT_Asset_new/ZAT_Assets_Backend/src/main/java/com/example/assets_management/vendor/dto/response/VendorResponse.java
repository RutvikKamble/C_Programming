package com.example.assets_management.vendor.dto.response;
import com.example.assets_management.vendor.entity.Vendor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Vendor response.
 */
public record VendorResponse(
        UUID id,
        UUID companyId,
        String companyName,
        String name,
        String contactEmail,
        String contactPhone,
        String address,
        LocalDateTime createdAt
) {
}
