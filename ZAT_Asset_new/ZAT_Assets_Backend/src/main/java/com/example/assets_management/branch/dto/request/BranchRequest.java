package com.example.assets_management.branch.dto.request;
import com.example.assets_management.branch.entity.Branch;

import java.util.UUID;

/**
 * DTO for creating or updating a Branch.
 */
public record BranchRequest(
        UUID companyId,
        String branchName,
        String address,
        String city,
        String state,
        String country
) {
}
