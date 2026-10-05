package com.example.assets_management.permission.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * @param permissionKey {@code application.module.action}, e.g. {@code asset.purchaseorder.create}
 *                      (stored as {@code Asset.Purchaseorder.Create})
 */
public record PermissionCreateRequest(
        @NotBlank(message = "permissionKey is required")
        @Size(max = 255)
        String permissionKey,

        @Size(max = 255)
        String description
) {
}
