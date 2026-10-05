package com.example.assets_management.permission.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PermissionKeyValidatorTest {

    @Test
    void shouldAcceptValidPermissionKey() {
        assertTrue(PermissionKeyValidator.isValid("Asset.Asset_Master.Create"));
        assertTrue(PermissionKeyValidator.isValid("Asset.Asset_Request.Approve_Manager"));
    }

    @Test
    void shouldRejectInvalidPermissionKey() {
        assertFalse(PermissionKeyValidator.isValid(null));
        assertFalse(PermissionKeyValidator.isValid(""));
        assertFalse(PermissionKeyValidator.isValid("asset_management.asset"));
        assertFalse(PermissionKeyValidator.isValid("Asset.Asset_Master.Create.extra"));
        assertFalse(PermissionKeyValidator.isValid("Asset_management.asset.create"));
        assertFalse(PermissionKeyValidator.isValid("asset_management.asset create"));
        assertFalse(PermissionKeyValidator.isValid("asset-management.asset.create"));
    }

    @Test
    void shouldParsePermissionKey() {
        String key = "Asset.Asset_Master.Create";

        assertEquals("Asset", PermissionKeyValidator.application(key));
        assertEquals("Asset_Master", PermissionKeyValidator.module(key));
        assertEquals("Create", PermissionKeyValidator.action(key));
    }

    @Test
    void shouldRejectInvalidKeyWhenParsing() {
        assertThrows(
                IllegalArgumentException.class,
                () -> PermissionKeyValidator.application("invalid")
        );
    }

    @Test
    void normalizesLowercaseApplicationModuleActionKeys() {
        assertEquals("Asset.Purchaseorder.Create", PermissionKeyValidator.normalize("asset.purchaseorder.create"));
        assertEquals("Asset.Asset_Master.View", PermissionKeyValidator.normalize(" asset.asset_master.view "));
        assertTrue(PermissionKeyValidator.isValid(PermissionKeyValidator.normalize("asset.purchaseorder.create")));
    }

    @Test
    void normalizeLeavesMalformedKeysForValidationToReject() {
        assertEquals("asset.purchaseorder", PermissionKeyValidator.normalize("asset.purchaseorder"));
        assertFalse(PermissionKeyValidator.isValid(PermissionKeyValidator.normalize("crm.leads.view")));
    }
}
