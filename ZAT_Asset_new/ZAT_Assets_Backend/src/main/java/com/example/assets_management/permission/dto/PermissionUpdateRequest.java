package com.example.assets_management.permission.dto;

import jakarta.validation.constraints.Size;

public record PermissionUpdateRequest(
        @Size(max = 255)
        String description
) {
}
