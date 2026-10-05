package com.example.assets_management.user_role.controller;

import com.example.assets_management.user_role.dto.request.UserRoleAssignRequest;
import com.example.assets_management.user_role.dto.response.UserRoleResponse;
import com.example.assets_management.user_role.service.UserRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping({"/api/users","/api/v1/users"})
@RequiredArgsConstructor
public class UserRoleController {

    private final UserRoleService userRoleService;

    @GetMapping("/{userId}/role")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('Asset.User.View') or @currentUserService.requireUserId().equals(#userId)")
    public ResponseEntity<UserRoleResponse> getUserRole(
            @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(
                userRoleService.getUserRole(userId)
        );
    }

    @PutMapping("/{userId}/role")
    @org.springframework.security.access.prepost.PreAuthorize("hasAuthority('Asset.User.Assign_Role')")
    public ResponseEntity<UserRoleResponse> assignRole(
            @PathVariable UUID userId,
            @Valid @RequestBody UserRoleAssignRequest request
    ) {
        return ResponseEntity.ok(
                userRoleService.assignRole(userId, request)
        );
    }
}
