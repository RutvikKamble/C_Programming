package com.example.assets_management.vendor.dto.request;
import com.example.assets_management.vendor.entity.Vendor;

import java.util.UUID;

/**
 * DTO for creating or updating a Vendor.
 */
public record VendorRequest(
        UUID companyId,
        String name,
        String contactEmail,
        String contactPhone,
        String address
) {
}
