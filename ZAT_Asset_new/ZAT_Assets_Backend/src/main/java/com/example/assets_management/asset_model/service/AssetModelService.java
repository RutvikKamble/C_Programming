package com.example.assets_management.asset_model.service;
import com.example.assets_management.asset_model.entity.AssetModel;

import com.example.assets_management.asset_model.dto.request.AssetModelRequest;
import com.example.assets_management.asset_model.dto.response.AssetModelResponse;

import java.util.List;
import java.util.UUID;

/**
 * Service contract for managing {@link AssetModel} entities.
 */
public interface AssetModelService {

    /**
     * Return a list of models, optionally filtered by category id.
     *
     * @param categoryId optional category filter
     * @param name       optional name search
     * @return list of model responses
     */
    List<AssetModelResponse> findAll(UUID categoryId, String name);

    /**
     * Fetch a single model by id.
     *
     * @param id model id
     * @return the matching model response
     */
    AssetModelResponse findById(UUID id);

    /**
     * Create a new model from the given request.
     *
     * @param request creation payload
     * @return the persisted model response
     */
    AssetModelResponse create(AssetModelRequest request);

    /**
     * Update an existing model.
     *
     * @param id      id of the model to update
     * @param request update payload
     * @return the updated model response
     */
    AssetModelResponse update(UUID id, AssetModelRequest request);

    /**
     * Delete a model by id.
     *
     * @param id id of the model to delete
     */
    void delete(UUID id);
}
