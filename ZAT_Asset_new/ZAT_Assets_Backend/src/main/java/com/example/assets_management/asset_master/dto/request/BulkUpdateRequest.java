package com.example.assets_management.asset_master.dto.request;
import com.example.assets_management.asset_master.entity.Asset;

import com.example.assets_management.asset_master.entity.Asset;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BulkUpdateRequest {

    private List<Asset> assets;

}