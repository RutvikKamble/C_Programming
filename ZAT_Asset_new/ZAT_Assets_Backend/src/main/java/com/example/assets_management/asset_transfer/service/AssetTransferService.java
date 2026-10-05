package com.example.assets_management.asset_transfer.service;

import com.example.assets_management.asset_transfer.dto.TransferRequest;
import com.example.assets_management.asset_transfer.entity.AssetTransfer;

import java.util.List;
import java.util.UUID;

public interface AssetTransferService {

    AssetTransfer transfer(UUID assetId, TransferRequest request);

    List<AssetTransfer> findByAssetId(UUID assetId);

    AssetTransfer findById(UUID id);

    void delete(UUID id);
}
