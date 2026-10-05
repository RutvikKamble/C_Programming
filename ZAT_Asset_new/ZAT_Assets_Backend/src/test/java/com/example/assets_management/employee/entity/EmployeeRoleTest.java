package com.example.assets_management.employee.entity;

import com.example.assets_management.role.entity.Role;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeRoleTest {

    @Test
    void shouldAssignRoleToEmployee() {
        Employee employee = new Employee();
        Role role = new Role();

        employee.setRole(role);

        assertSame(role, employee.getRole());
    }

    @Test
    void shouldAllowRoleToBeCleared() {
        Employee employee = new Employee();
        Role role = new Role();

        employee.setRole(role);
        employee.setRole(null);

        assertNull(employee.getRole());
    }

    @Test
    void shouldUseUuidForEmployeeId() throws NoSuchFieldException {
        Field idField = Employee.class.getDeclaredField("id");

        assertEquals(UUID.class, idField.getType());
    }

    @Test
    void shouldContainRoleField() throws NoSuchFieldException {
        Field roleField = Employee.class.getDeclaredField("role");

        assertEquals(Role.class, roleField.getType());
    }
}
