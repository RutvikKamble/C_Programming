package com.example.assets_management.role.controller;

import com.example.assets_management.role.dto.request.RoleCreateRequest;
import com.example.assets_management.role.dto.request.RoleUpdateRequest;
import com.example.assets_management.role.dto.response.RoleResponse;
import com.example.assets_management.role.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping({"/api/roles", "/api/v1/roles"})
@RequiredArgsConstructor
public class RoleController {
 private final RoleService roleService;
 @GetMapping @PreAuthorize("hasAuthority('Asset.Role.View')") public ResponseEntity<List<RoleResponse>> getAll(){return ResponseEntity.ok(roleService.getAll());}
 @GetMapping("/{roleId}") @PreAuthorize("hasAuthority('Asset.Role.View')") public ResponseEntity<RoleResponse> getById(@PathVariable UUID roleId){return ResponseEntity.ok(roleService.getById(roleId));}
 @PostMapping @PreAuthorize("hasAuthority('Asset.Role.Create')") public ResponseEntity<RoleResponse> createCustomRole(@Valid @RequestBody RoleCreateRequest request){return ResponseEntity.status(HttpStatus.CREATED).body(roleService.createCustomRole(request));}
 @PutMapping("/{roleId}") @PatchMapping("/{roleId}") @PreAuthorize("hasAuthority('Asset.Role.Update')") public ResponseEntity<RoleResponse> updateCustomRole(@PathVariable UUID roleId,@Valid @RequestBody RoleUpdateRequest request){return ResponseEntity.ok(roleService.updateCustomRole(roleId,request));}
 @PatchMapping("/{roleId}/activate") @PreAuthorize("hasAuthority('Asset.Role.Update')") public ResponseEntity<RoleResponse> activate(@PathVariable UUID roleId){return ResponseEntity.ok(roleService.activate(roleId));}
 @PatchMapping("/{roleId}/deactivate") @PreAuthorize("hasAuthority('Asset.Role.Deactivate')") public ResponseEntity<RoleResponse> deactivate(@PathVariable UUID roleId){return ResponseEntity.ok(roleService.deactivate(roleId));}
 @DeleteMapping("/{roleId}") @PreAuthorize("hasAuthority('Asset.Role.Deactivate')") public ResponseEntity<String> delete(@PathVariable UUID roleId){roleService.deleteCustomRole(roleId);return ResponseEntity.ok("Custom role deleted successfully");}
}
