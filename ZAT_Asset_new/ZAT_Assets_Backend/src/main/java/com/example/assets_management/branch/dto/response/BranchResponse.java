package com.example.assets_management.branch.dto.response;
import com.example.assets_management.branch.entity.Branch;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Branch response.
 */
public record BranchResponse(
        UUID id,
        UUID companyId,
        String companyName,
        String branchName,
        String address,
        String city,
        String state,
        String country,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
