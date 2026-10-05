package com.example.assets_management.asset_master.service;
import com.example.assets_management.asset_master.entity.Asset;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface AssetService {

    List<Asset> findAll();

    Optional<Asset> findById(UUID id);

    Asset save(Asset asset);

    Asset update(UUID id, Asset asset);

    void delete(UUID id);

    void bulkDelete(List<UUID> ids);

    List<Asset> bulkUpdate(List<Asset> assets);

    Asset patch(UUID id, Map<String, Object> updates);

    void importAssets(MultipartFile file) throws IOException;

    void exportAssets(HttpServletResponse response) throws IOException;

}
