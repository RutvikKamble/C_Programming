package com.example.assets_management.asset_documents.controller;

import com.example.assets_management.asset_documents.service.AssetDocumentService;
import com.example.assets_management.asset_documents.entity.AssetDocument;
import com.example.assets_management.asset_documents.enums.AssetDocumentType;
import jakarta.validation.Valid;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/asset-documents")
public class AssetDocumentController {
    private final AssetDocumentService documentService;

    public AssetDocumentController(AssetDocumentService documentService) {
        this.documentService = documentService;
    }

    @PreAuthorize("hasAuthority('Asset.Asset_Document.Upload')")
    @PostMapping
    public ResponseEntity<AssetDocument> createDocument(@Valid @RequestBody AssetDocument document) {
        return new ResponseEntity<>(documentService.createDocument(document), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('Asset.Asset_Document.Upload')")
    @PutMapping("/{id}")
    public ResponseEntity<AssetDocument> updateDocument(@PathVariable Long id, @Valid @RequestBody AssetDocument document) {
        return ResponseEntity.ok(documentService.updateDocument(id, document));
    }

    @PreAuthorize("hasAuthority('Asset.Asset_Document.Delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAuthority('Asset.Asset_Document.View')")
    @GetMapping("/{id}")
    public ResponseEntity<AssetDocument> getDocumentById(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    @PreAuthorize("hasAuthority('Asset.Asset_Document.View')")
    @GetMapping
    public ResponseEntity<List<AssetDocument>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    @PreAuthorize("hasAuthority('Asset.Asset_Document.View')")
    @GetMapping("/asset/{assetId}")
    public ResponseEntity<List<AssetDocument>> getDocumentsByAssetId(@PathVariable UUID assetId) {
        return ResponseEntity.ok(documentService.getDocumentsByAssetId(assetId));
    }

    @PreAuthorize("hasAuthority('Asset.Asset_Document.Upload')")
    @PostMapping(value = "/upload/{assetId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AssetDocument> upload(@PathVariable UUID assetId, @RequestParam("file") MultipartFile file,
                                                @RequestParam AssetDocumentType documentType, @RequestParam(required = false) String uploadedBy,
                                                @RequestParam(required = false) LocalDate expiryDate) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentService.upload(assetId, file, documentType, uploadedBy, expiryDate));
    }

    @PreAuthorize("hasAuthority('Asset.Asset_Document.Download')")
    @GetMapping("/{id}/file")
    public ResponseEntity<InputStreamResource> download(@PathVariable Long id) throws IOException {
        Path p = documentService.resolveFile(id);
        MediaType mt = MediaType.APPLICATION_OCTET_STREAM;
        String type = Files.probeContentType(p);
        if (type != null) try {
            mt = MediaType.parseMediaType(type);
        } catch (Exception ignored) {
        }
        return ResponseEntity.ok().contentType(mt).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + p.getFileName() + "\"").contentLength(Files.size(p)).body(new InputStreamResource(Files.newInputStream(p)));
    }
}
