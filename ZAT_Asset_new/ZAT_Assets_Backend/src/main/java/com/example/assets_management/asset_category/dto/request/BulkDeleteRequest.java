package com.example.assets_management.asset_category.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class BulkDeleteRequest {
    private List<UUID> ids;
}
