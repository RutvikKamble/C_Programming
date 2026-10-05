package com.example.assets_management.asset_category.service;
import com.example.assets_management.asset_category.entity.AssetCategory;
import com.example.assets_management.asset_category.repository.AssetCategoryRepository;

import com.example.assets_management.company.entity.Company;
import com.example.assets_management.asset_category.enums.DepreciationMethod;
import com.example.assets_management.asset_category.dto.request.AssetCategoryRequest;
import com.example.assets_management.asset_category.dto.request.AssetCategoryUpdateDTO;
import com.example.assets_management.asset_category.dto.request.SubCategoryRequest;
import com.example.assets_management.company.repository.CompanyRepository;   // we need to create this repository
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;

@Service
public class AssetCategoryServiceImpl implements AssetCategoryService {

    private final AssetCategoryRepository categoryRepository;
    private final CompanyRepository companyRepository;

    public AssetCategoryServiceImpl(AssetCategoryRepository categoryRepository,
                                    CompanyRepository companyRepository) {
        this.categoryRepository = categoryRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    @Transactional
    public void saveCategory(AssetCategoryRequest request) {
        // Validate company
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        // Create parent
        AssetCategory parent = new AssetCategory();
        parent.setCompany(company);
        parent.setAssetCategory(request.getName());
        parent.setDepreciationMethod(request.getDepreciationMethod());
        parent.setUsefulLifeMonths(request.getUsefulLifeMonths());
        // Set parent if provided
        if (request.getParentCategoryId() != null) {
            AssetCategory parentCat = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            parent.setParentCategory(parentCat);
        }
        categoryRepository.save(parent);

        // Sub-categories
        if (request.getSubCategories() != null && !request.getSubCategories().isEmpty()) {
            for (SubCategoryRequest sub : request.getSubCategories()) {
                AssetCategory child = new AssetCategory();
                child.setCompany(company);
                child.setAssetCategory(sub.getName());
                child.setDepreciationMethod(sub.getDepreciationMethod());
                child.setUsefulLifeMonths(sub.getUsefulLifeMonths());
                child.setParentCategory(parent);
                categoryRepository.save(child);
            }
        }
    }

    @Override
    public AssetCategory updateCategory(UUID id, AssetCategory category) {
        AssetCategory existing = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        if (category.getAssetCategory() != null) existing.setAssetCategory(category.getAssetCategory());
        if (category.getDepreciationMethod() != null) existing.setDepreciationMethod(category.getDepreciationMethod());
        if (category.getUsefulLifeMonths() != null) existing.setUsefulLifeMonths(category.getUsefulLifeMonths());


        //  SAFE parentCategory handling
        if (category.getParentCategory() != null && category.getParentCategory().getId() != null) {
            AssetCategory parent = categoryRepository.findById(category.getParentCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
            existing.setParentCategory(parent);
        }

        //  SAFE company handling
        if (category.getCompany() != null && category.getCompany().getId() != null) {
            Company company = companyRepository.findById(category.getCompany().getId())
                    .orElseThrow(() -> new RuntimeException("Company not found"));
            existing.setCompany(company);
        }

        return categoryRepository.save(existing);
    }


    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        categoryRepository.detachChildren(Collections.singletonList(id));
        categoryRepository.deleteById(id);
    }

    @Override
    public AssetCategory getCategoryById(UUID id) {
        return categoryRepository.findById(id).orElse(null);
    }

    @Override
    public List<AssetCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    @Transactional
    public void bulkDelete(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) return;
        categoryRepository.detachChildren(ids);
        categoryRepository.deleteAllById(ids);
    }

    @Override
    @Transactional
    public List<AssetCategory> bulkUpdate(List<AssetCategoryUpdateDTO> updates) {
        List<AssetCategory> updatedList = new ArrayList<>();
        for (AssetCategoryUpdateDTO dto : updates) {
            AssetCategory existing = categoryRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Category not found: " + dto.getId()));
            if (dto.getName() != null) existing.setAssetCategory(dto.getName());
            if (dto.getDepreciationMethod() != null) existing.setDepreciationMethod(dto.getDepreciationMethod());
            if (dto.getUsefulLifeMonths() != null) existing.setUsefulLifeMonths(dto.getUsefulLifeMonths());
            if (dto.getParentCategoryId() != null) {
                AssetCategory parent = categoryRepository.findById(dto.getParentCategoryId())
                        .orElseThrow(() -> new RuntimeException("Parent not found"));
                existing.setParentCategory(parent);
            } else {
                existing.setParentCategory(null);
            }
            updatedList.add(categoryRepository.save(existing));
        }
        return updatedList;
    }

    @Override
    public void importCategories(MultipartFile file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line = reader.readLine(); // header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 5) continue; // need name, method, months, companyId, parentId (optional)
                String name = parts[0].trim();
                DepreciationMethod method = DepreciationMethod.valueOf(parts[1].trim().toUpperCase());
                Integer months = parts[2].trim().isEmpty() ? null : Integer.parseInt(parts[2].trim());
                UUID companyId = UUID.fromString(parts[3].trim());
                String parentIdStr = parts.length > 4 ? parts[4].trim() : "";

                Company company = companyRepository.findById(companyId)
                        .orElseThrow(() -> new RuntimeException("Company not found: " + companyId));

                AssetCategory category = new AssetCategory();
                category.setCompany(company);
                category.setAssetCategory(name);
                category.setDepreciationMethod(method);
                category.setUsefulLifeMonths(months);

                if (!parentIdStr.isEmpty()) {
                    UUID parentId = UUID.fromString(parentIdStr);
                    categoryRepository.findById(parentId).ifPresent(category::setParentCategory);
                }
                categoryRepository.save(category);
            }
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid data in CSV: " + e.getMessage());
        }
    }

    @Override
    public void exportCategories(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=categories.csv");

        List<AssetCategory> all = categoryRepository.findAll();
        try (PrintWriter writer = response.getWriter()) {
            writer.println("name,depreciationMethod,usefulLifeMonths,companyId,parentId");
            for (AssetCategory cat : all) {
                String parentId = cat.getParentCategory() != null ? cat.getParentCategory().getId().toString() : "";
                writer.printf("%s,%s,%d,%s,%s%n",
                        cat.getAssetCategory(),
                        cat.getDepreciationMethod().name(),
                        cat.getUsefulLifeMonths() != null ? cat.getUsefulLifeMonths() : 0,
                        cat.getCompany().getId().toString(),
                        parentId);
            }
            writer.flush();
        }
    }

    @Override
    @Transactional
    public AssetCategory patchCategory(UUID id, Map<String, Object> updates) {
        AssetCategory existing = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        updates.forEach((key, value) -> {
            switch (key) {
                case "assetCategory":
                    existing.setAssetCategory((String) value);
                    break;
                case "depreciationMethod":
                    existing.setDepreciationMethod(DepreciationMethod.valueOf(((String) value).toUpperCase()));
                    break;
                case "usefulLifeMonths":
                    existing.setUsefulLifeMonths(value != null ? Integer.parseInt(value.toString()) : null);
                    break;
                case "parentCategoryId":
                    if (value == null) {
                        existing.setParentCategory(null);
                    } else {
                        UUID parentId = UUID.fromString(value.toString());
                        categoryRepository.findById(parentId).ifPresent(existing::setParentCategory);
                    }
                    break;
                case "companyId":
                    UUID companyId = UUID.fromString(value.toString());
                    companyRepository.findById(companyId).ifPresent(existing::setCompany);
                    break;
                default:
                    // ignore
                    break;
            }
        });
        return categoryRepository.save(existing);
    }

    @Override
    @Transactional
    public AssetCategory createSubCategory(UUID parentId, SubCategoryRequest request) {

        // Find parent category
        AssetCategory parent = categoryRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent category not found: " + parentId));

        // Create child category
        AssetCategory child = new AssetCategory();

        // Child belongs to the same company as parent
        child.setCompany(parent.getCompany());

        // Child category name
        child.setAssetCategory(request.getName());

        // Child depreciation configuration
        child.setDepreciationMethod(request.getDepreciationMethod());

        // Child useful life
        child.setUsefulLifeMonths(request.getUsefulLifeMonths());

        // Set parent-child relationship
        child.setParentCategory(parent);

        // Save child
        return categoryRepository.save(child);
    }
}
