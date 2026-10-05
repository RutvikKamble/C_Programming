package com.example.assets_management.asset_documents.service;

import com.example.assets_management.asset_documents.entity.AssetDocument;
import com.example.assets_management.asset_documents.enums.AssetDocumentType;
import com.example.assets_management.asset_documents.repository.AssetDocumentRepository;
import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.asset_master.repository.AssetRepository;
import com.example.assets_management.exception.FileStorageException;
import com.example.assets_management.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AssetDocumentServiceImpl implements AssetDocumentService {
    private final AssetDocumentRepository documentRepository;
    private final AssetRepository assetRepository;
    private final Path storageRoot;

    public AssetDocumentServiceImpl(AssetDocumentRepository documentRepository,
                                    AssetRepository assetRepository,
                                    @Value("${DOCUMENT_STORAGE_DIR:uploads/documents}") String storageDir) {
        this.documentRepository = documentRepository;
        this.assetRepository = assetRepository;
        this.storageRoot = Paths.get(storageDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(storageRoot);
        } catch (IOException e) {
            throw new FileStorageException("Unable to initialize document storage");
        }
    }

    @Override
    public AssetDocument createDocument(AssetDocument document) {
        return documentRepository.save(document);
    }

    @Override
    public AssetDocument updateDocument(Long id, AssetDocument document) {
        AssetDocument existing = getDocumentById(id);
        existing.setAsset(document.getAsset());
        existing.setDocumentType(document.getDocumentType());
        existing.setDocumentName(document.getDocumentName());
        if (document.getFilePath() != null) existing.setFilePath(document.getFilePath());
        existing.setUploadedBy(document.getUploadedBy());
        existing.setUploadedDate(document.getUploadedDate());
        existing.setExpiryDate(document.getExpiryDate());
        return documentRepository.save(existing);
    }

    @Override
    public void deleteDocument(Long id) {
        AssetDocument d = getDocumentById(id);
        deletePhysical(d);
        documentRepository.delete(d);
    }

    @Override
    public AssetDocument getDocumentById(Long id) {
        return documentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("AssetDocument not found with id: " + id));
    }

    @Override
    public List<AssetDocument> getAllDocuments() {
        return documentRepository.findAll();
    }

    @Override
    public List<AssetDocument> getDocumentsByAssetId(UUID assetId) {
        return documentRepository.findByAsset_Id(assetId);
    }

    @Override
    public AssetDocument upload(UUID assetId, MultipartFile file, AssetDocumentType type, String uploadedBy, LocalDate expiryDate) {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("file is required");
        Asset asset = assetRepository.findById(assetId).orElseThrow(() -> new ResourceNotFoundException("Asset not found: " + assetId));
        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "document" : file.getOriginalFilename());
        String safe = UUID.randomUUID() + "_" + original.replaceAll("[^a-zA-Z0-9._-]", "_");
        Path target = storageRoot.resolve(safe).normalize();
        if (!target.startsWith(storageRoot)) throw new IllegalArgumentException("Invalid file name");
        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileStorageException("Unable to store document");
        }
        AssetDocument d = new AssetDocument();
        d.setAsset(asset);
        d.setDocumentType(type);
        d.setDocumentName(original);
        d.setFilePath(target.toString());
        d.setUploadedBy(uploadedBy);
        d.setUploadedDate(LocalDateTime.now());
        d.setExpiryDate(expiryDate);
        return documentRepository.save(d);
    }

    @Override
    public Path resolveFile(Long id) {
        AssetDocument d = getDocumentById(id);
        if (d.getFilePath() == null) throw new ResourceNotFoundException("Document file is not available");
        Path p = Paths.get(d.getFilePath()).toAbsolutePath().normalize();
        if (!p.startsWith(storageRoot) || !Files.exists(p))
            throw new ResourceNotFoundException("Document file not found on disk");
        return p;
    }

    private void deletePhysical(AssetDocument d) {
        if (d.getFilePath() == null) return;
        try {
            Path p = Paths.get(d.getFilePath()).toAbsolutePath().normalize();
            if (p.startsWith(storageRoot)) Files.deleteIfExists(p);
        } catch (IOException ignored) {
        }
    }
}
