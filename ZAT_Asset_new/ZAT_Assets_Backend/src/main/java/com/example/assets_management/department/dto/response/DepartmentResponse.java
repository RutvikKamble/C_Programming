package com.example.assets_management.department.dto.response;
import com.example.assets_management.department.entity.Department;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Department response.
 */
public record DepartmentResponse(
        UUID id,
        UUID companyId,
        String companyName,
        UUID branchId,
        String branchName,
        String departmentName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
