package com.example.assets_management.barcode.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.example.assets_management.barcode.service.BarcodeService;

import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.barcode.dto.response.CodeValuesResponse;
import com.example.assets_management.barcode.dto.request.ScanRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller exposing QR code and barcode generation for assets.
 *
 * <p>Base path: {@code /api/barcode}.</p>
 *
 * <ul>
 *   <li>GET {@code /api/barcode/assets/{id}/qr} ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â€šÂ¬Ã‚Â QR code PNG</li>
 *   <li>GET {@code /api/barcode/assets/{id}/barcode} ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â€šÂ¬Ã‚Â barcode PNG</li>
 *   <li>GET {@code /api/barcode/assets/{id}/download} ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â€šÂ¬Ã‚Â QR PNG download</li>
 *   <li>GET {@code /api/barcode/assets/{id}/print} ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â€šÂ¬Ã‚Â HTML print view</li>
 *   <li>GET {@code /api/barcode/assets/{id}/values} ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â€šÂ¬Ã‚Â persisted code values</li>
 *   <li>POST {@code /api/barcode/scan} ÃƒÆ’Ã‚Â¢ÃƒÂ¢Ã¢â‚¬Å¡Ã‚Â¬ÃƒÂ¢Ã¢â€šÂ¬Ã‚Â resolve a scanned code to asset info</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/barcode")
@RequiredArgsConstructor
public class BarcodeController {

    private final BarcodeService barcodeService;

    /**
     * Serve the QR code for an asset as an inline PNG image.
     */
    @PreAuthorize("hasAuthority(\'Asset.QR_Barcode.View\')")
    @GetMapping(value = "/assets/{id}/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQr(@PathVariable UUID id) throws IOException {
        BufferedImage image = barcodeService.generateQrCode(id);
        return imageResponse(image, fileName(id, "qr"));
    }

    /**
     * Serve the barcode for an asset as an inline PNG image.
     */
    @PreAuthorize("hasAuthority(\'Asset.QR_Barcode.View\')")
    @GetMapping(value = "/assets/{id}/barcode", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getBarcode(@PathVariable UUID id) throws IOException {
        BufferedImage image = barcodeService.generateBarcode(id);
        return imageResponse(image, fileName(id, "barcode"));
    }

    /**
     * Download the QR code as an attachment.
     */
    @PreAuthorize("hasAuthority(\'Asset.QR_Barcode.Download\')")
    @GetMapping(value = "/assets/{id}/download", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> downloadQr(@PathVariable UUID id) throws IOException {
        BufferedImage image = barcodeService.generateQrCode(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + fileName(id, "qr"))
                .body(toBytes(image));
    }

    /**
     * Return an HTML print view for an asset's QR code.
     */
    @PreAuthorize("hasAuthority(\'Asset.QR_Barcode.Print\')")
    @GetMapping(value = "/assets/{id}/print", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> printQr(@PathVariable UUID id) throws IOException {
        BufferedImage image = barcodeService.generateQrCode(id);
        byte[] png = toBytes(image);
        String base64 = java.util.Base64.getEncoder().encodeToString(png);

        String html = "<!DOCTYPE html><html><head><title>Asset QR</title></head>"
                + "<body style=\"text-align:center;font-family:Arial,sans-serif;\">"
                + "<h2>" + id + "</h2>"
                + "<img src=\"data:image/png;base64," + base64
                + "\" style=\"width:300px;height:300px;\"/>"
                + "<script>window.onload = function(){ window.print(); };</script>"
                + "</body></html>";

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

    /**
     * Return the persisted QR / barcode values for an asset.
     */
    @PreAuthorize("hasAuthority(\'Asset.QR_Barcode.View\')")
    @GetMapping("/assets/{id}/values")
    public ResponseEntity<CodeValuesResponse> getCodeValues(@PathVariable UUID id) {
        Map<String, String> values = barcodeService.getCodeValues(id);
        CodeValuesResponse response = new CodeValuesResponse(
                UUID.fromString(values.get("assetId")),
                values.get("assetCode"),
                values.get("qrCodeValue"),
                values.get("barcodeValue")
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Resolve a scanned QR / barcode / asset code to the matching asset.
     */
    @PreAuthorize("hasAuthority(\'Asset.QR_Barcode.Scan\')")
    @PostMapping("/scan")
    public ResponseEntity<Asset> scan(@RequestBody ScanRequest request) {
        return ResponseEntity.ok(barcodeService.resolveCode(request.code()));
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private ResponseEntity<byte[]> imageResponse(BufferedImage image, String filename) throws IOException {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_PNG_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename)
                .body(toBytes(image));
    }

    private byte[] toBytes(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }

    private String fileName(UUID id, String type) {
        return "asset-" + id + "-" + type + ".png";
    }
}
