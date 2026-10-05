package com.example.assets_management.role_permission.dto.response;

import com.example.assets_management.permission.enums.PermissionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record RolePermissionResponse(
        UUID id,
        UUID roleId,
        String roleName,
        UUID permissionId,
        String permissionKey,
        PermissionStatus permissionStatus,
        LocalDateTime createdAt
) {
}
