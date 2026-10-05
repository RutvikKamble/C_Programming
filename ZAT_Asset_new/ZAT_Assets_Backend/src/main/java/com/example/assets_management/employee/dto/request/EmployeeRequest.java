package com.example.assets_management.employee.dto.request;
import com.example.assets_management.employee.entity.Employee;

import com.example.assets_management.employee.enums.UserStatus;
import java.util.UUID;

/**
 * DTO for creating or updating an Employee.
 */
public record EmployeeRequest(
        UUID companyId,
        UUID branchId,
        UUID departmentId,
        String employeeCode,
        String fullName,
        String email,
        String phone,
        String passwordHash,
        UserStatus status
) {
}
