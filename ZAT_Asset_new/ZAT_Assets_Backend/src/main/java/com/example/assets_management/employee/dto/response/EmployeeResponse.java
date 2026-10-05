package com.example.assets_management.employee.dto.response;
import com.example.assets_management.employee.entity.Employee;

import com.example.assets_management.employee.enums.UserStatus;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Employee response.
 */
public record EmployeeResponse(
        UUID id,
        UUID companyId,
        String companyName,
        UUID branchId,
        String branchName,
        UUID departmentId,
        String departmentName,
        String employeeCode,
        String fullName,
        String email,
        String phone,
        UserStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
