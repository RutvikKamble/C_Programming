package com.example.assets_management.audit_run.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record AuditRunCreateRequest(
        @NotBlank String name,
        UUID companyId,
        UUID branchId,
        UUID categoryId,
        String auditedBy,
        String notes
) {}
