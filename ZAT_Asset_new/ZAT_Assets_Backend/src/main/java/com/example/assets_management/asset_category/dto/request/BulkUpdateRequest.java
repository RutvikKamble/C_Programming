package com.example.assets_management.asset_category.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class BulkUpdateRequest {
    private List<AssetCategoryUpdateDTO> categories;
}
