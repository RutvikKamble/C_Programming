package com.example.assets_management.asset_category.dto.request;

import com.example.assets_management.asset_category.enums.DepreciationMethod;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SubCategoryRequest {
    private String name;
    private DepreciationMethod depreciationMethod;
    private Integer usefulLifeMonths;
}
