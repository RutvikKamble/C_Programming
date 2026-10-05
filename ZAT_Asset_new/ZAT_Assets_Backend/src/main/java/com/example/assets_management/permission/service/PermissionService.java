package com.example.assets_management.permission.service;

import com.example.assets_management.permission.dto.PermissionCreateRequest;
import com.example.assets_management.permission.dto.PermissionResponse;
import com.example.assets_management.permission.dto.PermissionUpdateRequest;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PermissionService {

    List<PermissionResponse> getAll();

    /** module -> permissions, for permission pickers. */
    Map<String, List<PermissionResponse>> getGroupedByModule();

    PermissionResponse getById(UUID id);

    /** Creates a custom permission ({@code application.module.action}) and gives it to the Super Admin role. */
    PermissionResponse create(PermissionCreateRequest request);

    PermissionResponse update(UUID id, PermissionUpdateRequest request);

    PermissionResponse activate(UUID id);

    PermissionResponse deactivate(UUID id);

    /** Deletes a custom permission and removes it from every role and user. */
    void delete(UUID id);
}
