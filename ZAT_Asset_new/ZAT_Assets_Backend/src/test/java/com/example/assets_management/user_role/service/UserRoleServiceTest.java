package com.example.assets_management.user_role.service;

import com.example.assets_management.employee.entity.Employee;
import com.example.assets_management.employee.repository.EmployeeRepository;
import com.example.assets_management.role.entity.Role;
import com.example.assets_management.role.enums.RoleStatus;
import com.example.assets_management.role.enums.RoleType;
import com.example.assets_management.role.repository.RoleRepository;
import com.example.assets_management.user_role.dto.request.UserRoleAssignRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRoleServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserRoleServiceImpl service;

    @Test
    void shouldAssignActiveRoleToUser() {
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        Employee user = new Employee();
        user.setId(userId);
        user.setEmployeeCode("EMP-001");
        user.setFullName("Test User");

        Role role = new Role();
        role.setId(roleId);
        role.setName("Asset Manager");
        role.setType(RoleType.SYSTEM);
        role.setStatus(RoleStatus.ACTIVE);

        when(employeeRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(roleRepository.findById(roleId))
                .thenReturn(Optional.of(role));
        when(employeeRepository.save(user))
                .thenReturn(user);

        var response = service.assignRole(
                userId,
                new UserRoleAssignRequest(roleId)
        );

        assertEquals(userId, response.userId());
        assertEquals(roleId, response.roleId());
        assertEquals("Asset Manager", response.roleName());

        verify(employeeRepository).save(user);
    }

    @Test
    void shouldRejectInactiveRoleAssignment() {
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        Employee user = new Employee();
        user.setId(userId);

        Role role = new Role();
        role.setId(roleId);
        role.setStatus(RoleStatus.INACTIVE);

        when(employeeRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(roleRepository.findById(roleId))
                .thenReturn(Optional.of(role));

        assertThrows(
                IllegalStateException.class,
                () -> service.assignRole(
                        userId,
                        new UserRoleAssignRequest(roleId)
                )
        );

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void shouldRejectMissingUser() {
        UUID userId = UUID.randomUUID();
        UUID roleId = UUID.randomUUID();

        when(employeeRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> service.assignRole(
                        userId,
                        new UserRoleAssignRequest(roleId)
                )
        );

        verify(roleRepository, never()).findById(any(UUID.class));
    }
}
