package com.example.assets_management.user_permission.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

/** The complete set of permissions the user should effectively hold after the call. */
public record UserPermissionReplaceRequest(
        @NotNull(message = "permissionIds is required")
        List<UUID> permissionIds
) {
}
