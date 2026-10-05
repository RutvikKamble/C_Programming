package com.example.assets_management.asset_software.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public record BulkLicenseAllocationRequest(
        @NotEmpty List<UUID> employeeIds,
        @JsonProperty("notify") Boolean notifyUser,
        @Size(max = 150) String changedBy
) {}
