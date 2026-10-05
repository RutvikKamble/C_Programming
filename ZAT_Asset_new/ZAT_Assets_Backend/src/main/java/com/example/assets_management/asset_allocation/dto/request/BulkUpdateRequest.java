package com.example.assets_management.asset_allocation.dto.request;
import com.example.assets_management.asset_allocation.entity.Allocation;

import com.example.assets_management.asset_allocation.entity.Allocation;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BulkUpdateRequest {

    private List<Allocation> allocations;

}