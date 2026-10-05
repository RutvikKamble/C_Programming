package com.example.assets_management.company.dto.request;
import com.example.assets_management.company.entity.Company;

/**
 * DTO for creating or updating a Company.
 */
public record CompanyRequest(
        String companyName,
        String legalName,
        String gstinTaxId
) {
}
