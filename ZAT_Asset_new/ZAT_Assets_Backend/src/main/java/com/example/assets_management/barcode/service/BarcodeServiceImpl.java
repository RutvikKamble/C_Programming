package com.example.assets_management.barcode.service;

import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.asset_master.repository.AssetRepository;
import com.example.assets_management.exception.ResourceNotFoundException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.oned.Code128Writer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Default implementation of {@link BarcodeService} using ZXing.
 *
 * <p>Generates QR codes (and Code128 barcodes) for assets, persists the
 * generated values back onto the {@link Asset} entity, and resolves scanned
 * codes to the matching asset.</p>
 */
@Service
@RequiredArgsConstructor
public class BarcodeServiceImpl implements BarcodeService {

    private static final int QR_SIZE = 300;
    private static final int BARCODE_WIDTH = 300;
    private static final int BARCODE_HEIGHT = 120;

    private final AssetRepository assetRepository;

    @Override
    public BufferedImage generateQrCode(UUID assetId) {
        Asset asset = getAsset(assetId);

        String qrValue = asset.getQrCodeValue();
        if (qrValue == null || qrValue.isBlank()) {
            qrValue = "AST:" + asset.getAssetCode() + ":" + asset.getId();
            asset.setQrCodeValue(qrValue);
            assetRepository.save(asset);
        }

        return encodeQr(qrValue);
    }

    @Override
    public BufferedImage generateBarcode(UUID assetId) {
        Asset asset = getAsset(assetId);

        String barcodeValue = asset.getBarcodeValue();
        if (barcodeValue == null || barcodeValue.isBlank()) {
            barcodeValue = asset.getAssetCode() == null
                    ? asset.getId().toString().substring(0, 12)
                    : asset.getAssetCode();
            asset.setBarcodeValue(barcodeValue);
            assetRepository.save(asset);
        }

        return encodeBarcode(barcodeValue);
    }

    @Override
    public Asset resolveCode(String code) {
        if (code == null || code.isBlank()) {
            throw new ResourceNotFoundException("Code is required");
        }

        String trimmed = code.trim();

        // 1) match by QR value
        Optional<Asset> byQr = assetRepository.findByQrCodeValue(trimmed);
        if (byQr.isPresent()) {
            return byQr.get();
        }

        // 2) match by barcode value
        Optional<Asset> byBarcode = assetRepository.findByBarcodeValue(trimmed);
        if (byBarcode.isPresent()) {
            return byBarcode.get();
        }

        // 3) match by asset code
        Optional<Asset> byCode = assetRepository.findByAssetCode(trimmed);
        if (byCode.isPresent()) {
            return byCode.get();
        }

        throw new ResourceNotFoundException("No asset found for code: " + trimmed);
    }

    @Override
    public Map<String, String> getCodeValues(UUID assetId) {
        Asset asset = getAsset(assetId);

        Map<String, String> values = new HashMap<>();
        values.put("assetId", asset.getId().toString());
        values.put("assetCode", asset.getAssetCode());
        values.put("qrCodeValue", asset.getQrCodeValue());
        values.put("barcodeValue", asset.getBarcodeValue());
        return values;
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private Asset getAsset(UUID assetId) {
        return assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found: " + assetId));
    }

    private BufferedImage encodeQr(String content) {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 1);

        try {
            BitMatrix matrix = new QRCodeWriter().encode(
                    content, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE, hints);
            return MatrixToImageWriter.toBufferedImage(matrix);
        } catch (WriterException e) {
            throw new IllegalStateException("Failed to generate QR code", e);
        }
    }

    private BufferedImage encodeBarcode(String content) {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.MARGIN, 2);

        try {
            BitMatrix matrix = new Code128Writer().encode(
                    content, BarcodeFormat.CODE_128, BARCODE_WIDTH, BARCODE_HEIGHT, hints);
            return MatrixToImageWriter.toBufferedImage(matrix);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Failed to generate barcode: " + content, e);
        }
    }
}
