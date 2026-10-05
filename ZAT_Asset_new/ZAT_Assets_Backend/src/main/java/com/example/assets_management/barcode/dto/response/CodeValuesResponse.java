package com.example.assets_management.barcode.dto.response;

import java.util.UUID;

/**
 * DTO for code values response.
 */
public record CodeValuesResponse(
        UUID assetId,
        String assetCode,
        String qrCodeValue,
        String barcodeValue
) {
}
