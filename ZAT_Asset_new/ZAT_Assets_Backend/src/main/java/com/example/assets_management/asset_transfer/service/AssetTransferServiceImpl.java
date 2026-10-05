package com.example.assets_management.asset_transfer.service;

import com.example.assets_management.asset_allocation.entity.Allocation;
import com.example.assets_management.asset_allocation.enums.AllocationStatus;
import com.example.assets_management.asset_allocation.repository.AllocationRepository;
import com.example.assets_management.asset_history.service.AssetHistoryService;
import com.example.assets_management.asset_history.enums.AssetHistoryEventType;
import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.asset_master.enums.AssetStatus;
import com.example.assets_management.asset_master.repository.AssetRepository;
import com.example.assets_management.asset_transfer.dto.TransferRequest;
import com.example.assets_management.asset_transfer.entity.AssetTransfer;
import com.example.assets_management.asset_transfer.repository.AssetTransferRepository;
import com.example.assets_management.branch.entity.Branch;
import com.example.assets_management.branch.repository.BranchRepository;
import com.example.assets_management.employee.entity.Employee;
import com.example.assets_management.employee.repository.EmployeeRepository;
import com.example.assets_management.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssetTransferServiceImpl implements AssetTransferService {

    private final AssetTransferRepository transferRepository;
    private final AssetRepository assetRepository;
    private final BranchRepository branchRepository;
    private final EmployeeRepository employeeRepository;
    private final AllocationRepository allocationRepository;
    private final AssetHistoryService assetHistoryService;

    @Override
    @Transactional
    public AssetTransfer transfer(UUID assetId, TransferRequest request) {
        if (request.toBranchId() == null && request.toEmployeeId() == null) {
            throw new IllegalArgumentException("At least one destination is required: toBranchId or toEmployeeId.");
        }

        Asset asset = assetRepository.findActiveById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Active asset not found: " + assetId));

        if (asset.getStatus() == AssetStatus.CLOSED || asset.getStatus() == AssetStatus.LOST) {
            throw new IllegalStateException("Asset cannot be transferred while its status is " + asset.getStatus());
        }

        Branch toBranch = null;
        if (request.toBranchId() != null) {
            toBranch = branchRepository.findById(request.toBranchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Destination branch not found: " + request.toBranchId()));
        }

        Employee toEmployee = null;
        if (request.toEmployeeId() != null) {
            toEmployee = employeeRepository.findById(request.toEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Destination employee not found: " + request.toEmployeeId()));

            if (toBranch == null) {
                toBranch = toEmployee.getBranch();
            } else if (toEmployee.getBranch() != null && !toBranch.getId().equals(toEmployee.getBranch().getId())) {
                throw new IllegalArgumentException("Destination employee does not belong to the destination branch.");
            }
        }

        // Capture the source state before mutating the Asset.
        Branch fromBranch = asset.getBranch();
        Employee fromEmployee = asset.getCurrentOwner();

        if (sameId(fromBranch, toBranch) && sameId(fromEmployee, toEmployee)) {
            throw new IllegalArgumentException("Transfer destination is the same as the current asset location/owner.");
        }

        AssetTransfer transfer = new AssetTransfer();
        transfer.setAsset(asset);
        transfer.setFromBranch(fromBranch);
        transfer.setToBranch(toBranch);
        transfer.setFromEmployee(fromEmployee);
        transfer.setToEmployee(toEmployee);
        transfer.setTransferDate(request.transferDate());
        transfer.setRemarks(request.remarks());

        // Update the live asset location/owner. A transfer does not pass through
        // the return/verify workflow and therefore does not make an assigned asset AVAILABLE.
        if (toBranch != null) {
            asset.setBranch(toBranch);
            asset.setCurrentLocation(toBranch.getBranchName());
        }

        if (toEmployee != null) {
            asset.setCurrentOwner(toEmployee);
            if (toEmployee.getDepartment() != null) {
                asset.setDepartment(toEmployee.getDepartment());
            }
            if (toBranch == null && toEmployee.getBranch() != null) {
                asset.setBranch(toEmployee.getBranch());
                asset.setCurrentLocation(toEmployee.getBranch().getBranchName());
            }
        }

        // Keep the active allocation synchronized with the new owner/location.
        // This does not close the allocation or execute a return; it preserves the
        // allocation's active state while the dedicated transfer record preserves
        // the old and new ownership values.

        /*
        allocationRepository.findByAssetIdAndStatus(assetId, AllocationStatus.ALLOCATED)
                .ifPresent(activeAllocation -> synchronizeActiveAllocation(activeAllocation, toBranch, toEmployee));
        */

        Allocation activeAllocation = allocationRepository
                .findByAssetIdAndStatus(assetId, AllocationStatus.ALLOCATED)
                .orElse(null);

        if (activeAllocation != null) {
            synchronizeActiveAllocation(activeAllocation, toBranch, toEmployee);
        }


        assetRepository.save(asset);
        AssetTransfer saved = transferRepository.save(transfer);

        String fromDescription = describe(fromEmployee, fromBranch);
        String toDescription = describe(toEmployee, toBranch);
        assetHistoryService.recordEvent(asset.getId(), AssetHistoryEventType.TRANSFERRED, "currentOwner/currentLocation", fromDescription, toDescription, "Asset transferred from " + fromDescription + " to " + toDescription, "SYSTEM"
        );

        return saved;
    }

    private void synchronizeActiveAllocation(Allocation allocation,
                                             Branch toBranch,
                                             Employee toEmployee) {
        if (toEmployee != null) {
            allocation.setEmployee(toEmployee);
            allocation.setDepartment(toEmployee.getDepartment());
            allocation.setBranch(toEmployee.getBranch() != null ? toEmployee.getBranch() : toBranch);
            allocation.setAcknowledgedByEmployee(Boolean.FALSE);
        }
        // For a branch-only movement the allocation keeps the employee's
        // original branch snapshot; the Asset entity is the live location source.
        allocationRepository.save(allocation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetTransfer> findByAssetId(UUID assetId) {
        if (!assetRepository.existsById(assetId)) {
            throw new ResourceNotFoundException("Asset not found: " + assetId);
        }
        return transferRepository.findByAssetIdOrderByTransferDateDescCreatedAtDesc(assetId);
    }

    @Override
    @Transactional(readOnly = true)
    public AssetTransfer findById(UUID id) {
        return transferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset transfer not found: " + id));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        AssetTransfer transfer = transferRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset transfer not found: " + id));
        transferRepository.delete(transfer);
    }

    private boolean sameId(Object first, Object second) {
        if (first == null || second == null) {
            return first == second;
        }
        if (first instanceof Branch firstBranch && second instanceof Branch secondBranch) {
            return firstBranch.getId().equals(secondBranch.getId());
        }
        if (first instanceof Employee firstEmployee && second instanceof Employee secondEmployee) {
            return firstEmployee.getId().equals(secondEmployee.getId());
        }
        return false;
    }

    private String describe(Employee employee, Branch branch) {
        String employeeText = employee != null ? employee.getFullName() : "No employee";
        String branchText = branch != null ? branch.getBranchName() : "No branch";
        return employeeText + " / " + branchText;
    }
}
