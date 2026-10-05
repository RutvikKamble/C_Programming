package com.example.assets_management.department.dto.request;
import com.example.assets_management.department.entity.Department;

import java.util.UUID;

/**
 * DTO for creating or updating a Department.
 */
public record DepartmentRequest(
        UUID companyId,
        UUID branchId,
        String departmentName
) {
}
