package com.example.assets_management.asset_category.service;
import com.example.assets_management.asset_category.entity.AssetCategory;

import com.example.assets_management.asset_category.dto.request.SubCategoryRequest;
import com.example.assets_management.asset_category.dto.request.AssetCategoryRequest;
import com.example.assets_management.asset_category.dto.request.AssetCategoryUpdateDTO;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AssetCategoryService {

    // Basic CRUD
    void saveCategory(AssetCategoryRequest request);
    AssetCategory updateCategory(UUID id, AssetCategory category);

    void deleteCategory(UUID id);
    AssetCategory getCategoryById(UUID id);
    List<AssetCategory> getAllCategories();

    // Sub-category
    AssetCategory createSubCategory(UUID parentId, SubCategoryRequest request);

    // Bulk operations
    void bulkDelete(List<UUID> ids);
    List<AssetCategory> bulkUpdate(List<AssetCategoryUpdateDTO> updates);

    // Import / Export
    void importCategories(MultipartFile file) throws IOException;
    void exportCategories(HttpServletResponse response) throws IOException;

    // Partial update (PATCH)
    AssetCategory patchCategory(UUID id, Map<String, Object> updates);
}
