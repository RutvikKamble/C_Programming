package com.example.assets_management.role_permission.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record RolePermissionAssignRequest(
        @NotNull(message = "permissionId is required")
        UUID permissionId
) {
}
