package com.example.assets_management.asset_documents.service;
import com.example.assets_management.asset_documents.entity.AssetDocument;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;
public interface AssetDocumentService {
    AssetDocument createDocument(AssetDocument document);
    AssetDocument updateDocument(Long id, AssetDocument document);
    void deleteDocument(Long id);
    AssetDocument getDocumentById(Long id);
    List<AssetDocument> getAllDocuments();
    List<AssetDocument> getDocumentsByAssetId(UUID assetId);
    AssetDocument upload(UUID assetId, MultipartFile file, com.example.assets_management.asset_documents.enums.AssetDocumentType type, String uploadedBy, java.time.LocalDate expiryDate);
    java.nio.file.Path resolveFile(Long id);
}
