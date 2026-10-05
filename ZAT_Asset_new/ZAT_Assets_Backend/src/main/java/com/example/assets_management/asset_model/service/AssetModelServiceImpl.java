package com.example.assets_management.asset_model.service;
import com.example.assets_management.asset_model.entity.AssetModel;
import com.example.assets_management.asset_model.repository.AssetModelRepository;

import com.example.assets_management.asset_category.entity.AssetCategory;
import com.example.assets_management.asset_category.repository.AssetCategoryRepository;
import com.example.assets_management.asset_model.dto.request.AssetModelRequest;
import com.example.assets_management.asset_model.dto.response.AssetModelResponse;
import com.example.assets_management.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Default implementation of {@link AssetModelService}.
 *
 * <p>Performs manual mapping between entities and DTOs in line with the
 * existing project conventions (no MapStruct). Uses constructor injection
 * and throws {@link ResourceNotFoundException} for missing entities.</p>
 */
@Service
public class AssetModelServiceImpl implements AssetModelService {

    private static final Logger log = LoggerFactory.getLogger(AssetModelServiceImpl.class);

    private final AssetModelRepository modelRepository;
    private final AssetCategoryRepository categoryRepository;

    public AssetModelServiceImpl(AssetModelRepository modelRepository,
                                 AssetCategoryRepository categoryRepository) {
        this.modelRepository = modelRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetModelResponse> findAll(UUID categoryId, String name) {
        List<AssetModel> result;

        if (name != null && !name.isBlank()) {
            result = modelRepository.findByNameContainingIgnoreCase(name.trim());
        } else if (categoryId != null) {
            result = modelRepository.findByCategoryId(categoryId);
        } else {
            result = modelRepository.findAll();
        }

        log.debug("Fetched {} asset models (categoryId={}, name={})", result.size(), categoryId, name);
        return result.stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AssetModelResponse findById(UUID id) {
        AssetModel model = modelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset Model not found: " + id));
        return toResponse(model);
    }

    @Override
    @Transactional
    public AssetModelResponse create(AssetModelRequest request) {
        AssetCategory category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset Category not found: " + request.categoryId()));

        AssetModel model = new AssetModel();
        model.setCategory(category);
        model.setName(request.name().trim());
        model.setSpecSheet(request.specSheet());
        model.setDefaultWarrantyMonths(request.defaultWarrantyMonths());
        model.setDepreciationMethod(request.depreciationMethod());

        AssetModel saved = modelRepository.save(model);
        log.info("Created asset model '{}' with id {}", saved.getName(), saved.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public AssetModelResponse update(UUID id, AssetModelRequest request) {
        AssetModel model = modelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset Model not found: " + id));

        if (request.categoryId() != null) {
            AssetCategory category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Asset Category not found: " + request.categoryId()));
            model.setCategory(category);
        }

        if (request.name() != null && !request.name().isBlank()) {
            model.setName(request.name().trim());
        }
        model.setSpecSheet(request.specSheet());
        model.setDefaultWarrantyMonths(request.defaultWarrantyMonths());
        model.setDepreciationMethod(request.depreciationMethod());

        AssetModel saved = modelRepository.save(model);
        log.info("Updated asset model '{}' with id {}", saved.getName(), saved.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        AssetModel model = modelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset Model not found: " + id));
        modelRepository.delete(model);
        log.info("Deleted asset model with id {}", id);
    }

    /**
     * Map an entity to its response DTO.
     */
    private AssetModelResponse toResponse(AssetModel model) {
        AssetCategory category = model.getCategory();
        return new AssetModelResponse(
                model.getId(),
                category != null ? category.getId() : null,
                category != null ? category.getAssetCategory() : null,
                model.getName(),
                model.getSpecSheet(),
                model.getDefaultWarrantyMonths(),
                model.getDepreciationMethod(),
                model.getCreatedAt()
        );
    }
}
