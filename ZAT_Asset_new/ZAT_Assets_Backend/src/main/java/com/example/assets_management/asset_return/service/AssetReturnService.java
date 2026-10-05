package com.example.assets_management.asset_return.service;

import com.example.assets_management.asset_return.entity.AssetReturn;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetReturnService {
    List<AssetReturn> findAll();
    Optional<AssetReturn> findById(UUID id);
    AssetReturn save(AssetReturn assetReturn);
    AssetReturn update(UUID id, AssetReturn assetReturn);
    AssetReturn verify(UUID id, UUID verifierId);
    AssetReturn complete(UUID id);
    void delete(UUID id);
}
