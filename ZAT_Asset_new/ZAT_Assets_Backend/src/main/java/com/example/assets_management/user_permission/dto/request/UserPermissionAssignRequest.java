package com.example.assets_management.user_permission.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserPermissionAssignRequest(
        @NotNull(message = "permissionId is required")
        UUID permissionId
) {
}
