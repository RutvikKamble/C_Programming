package com.example.assets_management.role.service;

import com.example.assets_management.role.dto.request.RoleCreateRequest;
import com.example.assets_management.role.dto.request.RoleUpdateRequest;
import com.example.assets_management.role.dto.response.RoleResponse;

import java.util.List;
import java.util.UUID;

public interface RoleService {

    List<RoleResponse> getAll();

    RoleResponse getById(UUID roleId);

    RoleResponse createCustomRole(RoleCreateRequest request);

    RoleResponse updateCustomRole(UUID roleId, RoleUpdateRequest request);

    RoleResponse activate(UUID roleId);

    RoleResponse deactivate(UUID roleId);

    void deleteCustomRole(UUID roleId);
}
