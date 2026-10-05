package com.example.assets_management.asset_technician.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BulkTechnicianUpdateRequest {

    @NotEmpty(message = "ids list cannot be empty")
    private List<Long> ids;

    @NotNull(message = "updateFields cannot be null")
    @Valid
    private TechnicianPatchDTO updateFields;
}