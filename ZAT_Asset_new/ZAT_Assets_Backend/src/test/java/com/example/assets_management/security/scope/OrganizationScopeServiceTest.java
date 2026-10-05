package com.example.assets_management.security.scope;

import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.asset_master.repository.AssetRepository;
import com.example.assets_management.branch.entity.Branch;
import com.example.assets_management.company.entity.Company;
import com.example.assets_management.security.context.CurrentUserService;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrganizationScopeServiceTest {

    private final CurrentUserService currentUserService = mock(CurrentUserService.class);
    private final AssetRepository assetRepository = mock(AssetRepository.class);
    private final OrganizationScopeService service =
            new OrganizationScopeService(currentUserService, assetRepository);

    @Test
    void allowsSameCompany() {
        UUID companyId = UUID.randomUUID();
        when(currentUserService.getCompanyId()).thenReturn(companyId);
        assertTrue(service.hasCompanyAccess(companyId));
    }

    @Test
    void rejectsDifferentCompany() {
        when(currentUserService.getCompanyId()).thenReturn(UUID.randomUUID());
        assertFalse(service.hasCompanyAccess(UUID.randomUUID()));
    }

    @Test
    void companyWideUserCanAccessAnyBranchInOwnCompany() {
        UUID companyId = UUID.randomUUID();
        when(currentUserService.getCompanyId()).thenReturn(companyId);
        when(currentUserService.getBranchId()).thenReturn(null);
        assertTrue(service.hasCompanyAndBranchAccess(companyId, UUID.randomUUID()));
    }

    @Test
    void branchUserCanAccessOwnBranch() {
        UUID companyId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();
        when(currentUserService.getCompanyId()).thenReturn(companyId);
        when(currentUserService.getBranchId()).thenReturn(branchId);
        assertTrue(service.hasCompanyAndBranchAccess(companyId, branchId));
    }

    @Test
    void branchUserCannotAccessAnotherBranch() {
        UUID companyId = UUID.randomUUID();
        UUID ownBranch = UUID.randomUUID();
        UUID otherBranch = UUID.randomUUID();
        when(currentUserService.getCompanyId()).thenReturn(companyId);
        when(currentUserService.getBranchId()).thenReturn(ownBranch);
        assertFalse(service.hasCompanyAndBranchAccess(companyId, otherBranch));
    }

    @Test
    void assetScopeUsesCompanyAndBranch() {
        UUID companyId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();
        Company company = new Company();
        company.setId(companyId);
        Branch branch = new Branch();
        branch.setId(branchId);
        Asset asset = new Asset();
        asset.setCompany(company);
        asset.setBranch(branch);

        when(currentUserService.getCompanyId()).thenReturn(companyId);
        when(currentUserService.getBranchId()).thenReturn(branchId);

        assertTrue(service.hasAssetAccess(asset));
    }

    @Test
    void missingAssetIsDenied() {
        UUID assetId = UUID.randomUUID();
        when(assetRepository.findById(assetId)).thenReturn(Optional.empty());
        assertFalse(service.hasAssetAccess(assetId));
    }
}
