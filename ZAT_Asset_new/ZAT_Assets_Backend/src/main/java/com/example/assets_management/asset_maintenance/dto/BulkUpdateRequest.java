package com.example.assets_management.asset_maintenance.dto;

import com.example.assets_management.asset_maintenance.entity.AssetMaintenance;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BulkUpdateRequest {
    private List<Long> ids;
    private AssetMaintenance updates;
}
