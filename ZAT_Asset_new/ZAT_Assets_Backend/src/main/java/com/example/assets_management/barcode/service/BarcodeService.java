package com.example.assets_management.barcode.service;

import com.example.assets_management.asset_master.entity.Asset;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.UUID;

/**
 * Service contract for the Barcode Module.
 *
 * <p>Responsible for generating QR codes and linear barcodes for assets
 * using the ZXing library, persisting the generated values back onto the
 * {@link Asset} entity, and resolving scanned codes to asset information.</p>
 */
public interface BarcodeService {

    /**
     * Generate (and persist) a QR code value for the given asset, returning
     * the QR code as a PNG image.
     *
     * @param assetId the asset id
     * @return a {@link BufferedImage} containing the QR code
     */
    BufferedImage generateQrCode(UUID assetId);

    /**
     * Generate (and persist) a linear barcode value for the given asset,
     * returning the barcode as a PNG image.
     *
     * @param assetId the asset id
     * @return a {@link BufferedImage} containing the barcode
     */
    BufferedImage generateBarcode(UUID assetId);

    /**
     * Resolve a scanned QR / barcode value to the matching asset.
     *
     * @param code the scanned value (asset code, QR value or barcode value)
     * @return the matching asset
     */
    Asset resolveCode(String code);

    /**
     * Return the persisted QR / barcode values for an asset.
     *
     * @param assetId the asset id
     * @return a map containing the asset code, qr value and barcode value
     */
    Map<String, String> getCodeValues(UUID assetId);
}
