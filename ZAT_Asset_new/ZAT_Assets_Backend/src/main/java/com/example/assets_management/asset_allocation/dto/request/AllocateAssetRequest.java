package com.example.assets_management.asset_allocation.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class AllocateAssetRequest {

    private UUID assetId;

    private UUID employeeId;

    private LocalDate allocatedOn;

    private Boolean acknowledgedByEmployee;

}