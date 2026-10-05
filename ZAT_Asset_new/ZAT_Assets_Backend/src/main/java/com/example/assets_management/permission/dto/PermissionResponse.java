package com.example.assets_management.permission.dto;

import com.example.assets_management.permission.enums.PermissionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record PermissionResponse(
        UUID id,
        String permissionKey,
        String application,
        String module,
        String action,
        String description,
        boolean systemPermission,
        PermissionStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
