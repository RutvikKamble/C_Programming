package com.example.assets_management.user_permission.dto.response;

import com.example.assets_management.user_permission.enums.UserPermissionSource;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * One permission row of one user.
 *
 * @param source  ROLE = inherited from the user's role, DIRECT = added for this user only
 * @param revoked true when an inherited permission was removed from this user only (not effective)
 */
public record UserPermissionResponse(
        UUID id,
        UUID userId,
        UUID permissionId,
        String permissionKey,
        UserPermissionSource source,
        LocalDateTime createdAt,
        boolean revoked
) {
}
