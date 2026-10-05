package com.example.assets_management.security.scope;

import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.asset_master.repository.AssetRepository;
import com.example.assets_management.security.context.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

/**
 * Company/branch authorization boundary for authenticated requests.
 *
 * Policy:
 * 1. A resource must belong to the authenticated user's company.
 * 2. A user without a branch assignment is company-wide within that company.
 * 3. A branch-scoped user may access resources in their branch and resources
 *    explicitly marked as company-level (branch == null).
 */
@Service("organizationScopeService")
@RequiredArgsConstructor
public class OrganizationScopeService {

    private final CurrentUserService currentUserService;
    private final AssetRepository assetRepository;

    public boolean hasCompanyAccess(UUID companyId) {
        UUID currentCompanyId = currentUserService.getCompanyId();
        return companyId != null
                && currentCompanyId != null
                && companyId.equals(currentCompanyId);
    }

    public boolean hasBranchAccess(UUID branchId) {
        UUID currentBranchId = currentUserService.getBranchId();
        return branchId != null
                && currentBranchId != null
                && branchId.equals(currentBranchId);
    }

    public boolean hasCompanyAndBranchAccess(UUID companyId, UUID branchId) {
        if (!hasCompanyAccess(companyId)) {
            return false;
        }

        UUID currentBranchId = currentUserService.getBranchId();
        if (currentBranchId == null) {
            return true;
        }

        return branchId == null || currentBranchId.equals(branchId);
    }

    public boolean hasAssetAccess(UUID assetId) {
        if (assetId == null) {
            return false;
        }

        return assetRepository.findById(assetId)
                .map(this::hasAssetAccess)
                .orElse(false);
    }

    public void requireAssetAccess(UUID assetId) {
        if (!hasAssetAccess(assetId)) {
            throw new AccessDeniedException("Asset is outside the authenticated company/branch scope");
        }
    }

    public boolean hasAssetAccess(Asset asset) {
        if (asset == null || asset.getCompany() == null || asset.getCompany().getId() == null) {
            return false;
        }

        UUID branchId = asset.getBranch() == null ? null : asset.getBranch().getId();
        return hasCompanyAndBranchAccess(asset.getCompany().getId(), branchId);
    }

    public boolean hasAllAssetAccess(Collection<UUID> assetIds) {
        return assetIds != null
                && !assetIds.isEmpty()
                && assetIds.stream().allMatch(this::hasAssetAccess);
    }

    public boolean hasAllAssetEntitiesAccess(Collection<Asset> assets) {
        return assets != null
                && !assets.isEmpty()
                && assets.stream().allMatch(this::hasAssetAccess);
    }

    public boolean hasDepartmentAccess(UUID departmentId) {
        return departmentId != null && java.util.Objects.equals(currentUserService.getDepartmentId(), departmentId);
    }

    public boolean hasOwnUserAccess(UUID userId) {
        return userId != null && java.util.Objects.equals(currentUserService.requireUserId(), userId);
    }

    public boolean isCurrentCompany(UUID companyId) {
        return Objects.equals(currentUserService.getCompanyId(), companyId);
    }
}
