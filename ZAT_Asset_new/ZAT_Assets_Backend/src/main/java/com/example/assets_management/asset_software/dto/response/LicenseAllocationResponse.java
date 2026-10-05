package com.example.assets_management.asset_software.dto.response;

import com.example.assets_management.asset_software.entity.LicenseAllocation;

import java.time.LocalDate;
import java.util.UUID;

public record LicenseAllocationResponse(
        UUID id,
        UUID softwareLicenseId,
        UUID employeeId,
        String employeeCode,
        String employeeName,
        String employeeEmail,
        LocalDate allocatedDate,
        LocalDate releasedDate,
        LicenseAllocation.AllocationStatus status
) {}
