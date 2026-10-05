package com.example.assets_management.asset_allocation.dto.response;

import com.example.assets_management.asset_allocation.enums.AllocationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class AllocationResponse {

    private UUID allocationId;

    private UUID assetId;

    private String assetCode;

    private String assetName;

    private UUID employeeId;

    private String employeeName;

    private String department;

    private String branch;

    private LocalDate allocatedOn;

    private LocalDate returnedOn;

    private AllocationStatus status;

    private Boolean acknowledgedByEmployee;

}