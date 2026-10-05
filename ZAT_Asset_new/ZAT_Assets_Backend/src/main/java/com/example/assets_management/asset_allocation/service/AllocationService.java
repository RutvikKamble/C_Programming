package com.example.assets_management.asset_allocation.service;
import com.example.assets_management.asset_allocation.entity.Allocation;
import com.example.assets_management.asset_allocation.dto.response.AllocationResponse;

import com.example.assets_management.asset_allocation.dto.request.AllocateAssetRequest;
import com.example.assets_management.asset_allocation.dto.request.DeallocateAssetRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface AllocationService {

    List<AllocationResponse> findAll();

    Optional<Allocation> findById(UUID id);

    Allocation save(Allocation allocation);

    Allocation update(UUID id, Allocation allocation);

    void delete(UUID id);

    Allocation allocate(AllocateAssetRequest request);

    Allocation deallocate(DeallocateAssetRequest request);

    Allocation getCurrentAllocation(UUID assetId);

    List<Allocation> getHistory(UUID assetId);

    List<Allocation> getByCategory(UUID categoryId);

    Allocation getAllocationDetails(UUID allocationId);

    Allocation patch(UUID id, Map<String,Object> updates);

    void bulkDelete(List<UUID> ids);

    List<Allocation> bulkUpdate(List<Allocation> allocations);

    /*
    void importAllocation(MultipartFile file) throws IOException;

    void exportAllocation(HttpServletResponse response) throws IOException;
    */

}