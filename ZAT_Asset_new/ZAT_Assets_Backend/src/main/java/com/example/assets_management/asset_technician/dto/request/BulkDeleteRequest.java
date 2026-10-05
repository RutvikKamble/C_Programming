package com.example.assets_management.asset_technician.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BulkDeleteRequest {

    @NotEmpty(message = "ids list cannot be empty")
    private List<Long> ids;
}
