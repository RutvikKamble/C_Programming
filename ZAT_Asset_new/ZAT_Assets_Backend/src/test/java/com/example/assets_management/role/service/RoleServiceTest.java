package com.example.assets_management.role.service;

import com.example.assets_management.role.dto.request.RoleCreateRequest;
import com.example.assets_management.role.dto.request.RoleUpdateRequest;
import com.example.assets_management.role.entity.Role;
import com.example.assets_management.role.enums.RoleStatus;
import com.example.assets_management.role.enums.RoleType;
import com.example.assets_management.role.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl service;

    @Test
    void shouldCreateCustomRole() {
        Role saved = new Role();
        saved.setId(UUID.randomUUID());
        saved.setName("Branch Auditor");
        saved.setDescription("Audit-only custom role");
        saved.setType(RoleType.CUSTOM);
        saved.setStatus(RoleStatus.ACTIVE);
        saved.setSystemDefined(false);

        when(roleRepository.findAll()).thenReturn(List.of());
        when(roleRepository.save(any(Role.class))).thenReturn(saved);

        var response = service.createCustomRole(
                new RoleCreateRequest(
                        "Branch Auditor",
                        "Audit-only custom role"
                )
        );

        assertEquals("Branch Auditor", response.name());
        assertEquals(RoleType.CUSTOM, response.type());
        assertEquals(RoleStatus.ACTIVE, response.status());
        assertFalse(response.systemDefined());

        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void shouldRejectDuplicateRoleName() {
        Role existing = new Role();
        existing.setId(UUID.randomUUID());
        existing.setName("Branch Auditor");

        when(roleRepository.findAll()).thenReturn(List.of(existing));

        assertThrows(
                IllegalStateException.class,
                () -> service.createCustomRole(
                        new RoleCreateRequest(
                                "branch auditor",
                                "duplicate"
                        )
                )
        );

        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void shouldRejectSystemRoleUpdate() {
        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setType(RoleType.SYSTEM);
        role.setSystemDefined(true);

        when(roleRepository.findById(role.getId()))
                .thenReturn(Optional.of(role));

        assertThrows(
                IllegalStateException.class,
                () -> service.updateCustomRole(
                        role.getId(),
                        new RoleUpdateRequest(
                                "Changed",
                                "Changed"
                        )
                )
        );
    }

    @Test
    void shouldRejectSystemRoleDelete() {
        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setType(RoleType.SYSTEM);
        role.setSystemDefined(true);

        when(roleRepository.findById(role.getId()))
                .thenReturn(Optional.of(role));

        assertThrows(
                IllegalStateException.class,
                () -> service.deleteCustomRole(role.getId())
        );

        verify(roleRepository, never()).delete(any(Role.class));
    }

    @Test
    void shouldDeactivateRole() {
        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setName("Branch Auditor");
        role.setType(RoleType.CUSTOM);
        role.setStatus(RoleStatus.ACTIVE);
        role.setSystemDefined(false);

        when(roleRepository.findById(role.getId()))
                .thenReturn(Optional.of(role));
        when(roleRepository.save(role))
                .thenReturn(role);

        var response = service.deactivate(role.getId());

        assertEquals(RoleStatus.INACTIVE, response.status());
    }
}
