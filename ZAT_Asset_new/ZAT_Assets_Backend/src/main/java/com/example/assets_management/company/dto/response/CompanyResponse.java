package com.example.assets_management.company.dto.response;
import com.example.assets_management.company.entity.Company;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Company response.
 */
public record CompanyResponse(
        UUID id,
        String companyName,
        String legalName,
        String gstinTaxId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
