package com.example.assets_management.asset_return.service;

import com.example.assets_management.asset_allocation.entity.Allocation;
import com.example.assets_management.asset_allocation.repository.AllocationRepository;
import com.example.assets_management.asset_history.service.AssetHistoryService;
import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.asset_master.enums.AssetStatus;
import com.example.assets_management.asset_return.entity.AssetReturn;
import com.example.assets_management.asset_return.enums.ReturnStatus;
import com.example.assets_management.asset_return.repository.AssetReturnRepository;
import com.example.assets_management.employee.entity.Employee;
import com.example.assets_management.employee.repository.EmployeeRepository;
import com.example.assets_management.exception.ResourceNotFoundException;
import com.example.assets_management.security.context.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssetReturnServiceImpl implements AssetReturnService {
    private final AssetReturnRepository assetReturnRepository;
    private final AllocationRepository allocationRepository;
    private final EmployeeRepository employeeRepository;
    private final AssetHistoryService historyService;
    private final CurrentUserService currentUserService;

    @Override public List<AssetReturn> findAll(){ return assetReturnRepository.findAll(); }
    @Override public Optional<AssetReturn> findById(UUID id){ return assetReturnRepository.findById(id); }

    @Override @Transactional
    public AssetReturn save(AssetReturn assetReturn) {
        if (assetReturn.getAllocation()==null || assetReturn.getAllocation().getId()==null)
            throw new IllegalArgumentException("allocation is required");
        Allocation allocation=allocationRepository.findById(assetReturn.getAllocation().getId())
                .orElseThrow(()->new ResourceNotFoundException("Allocation not found"));
        if (allocation.getStatus()==null || !allocation.getStatus().name().equals("ALLOCATED"))
            throw new IllegalStateException("Only an active allocation can have a return request");
        if (assetReturnRepository.findFirstByAllocationIdAndStatusIn(allocation.getId(), List.of(ReturnStatus.REQUESTED, ReturnStatus.VERIFIED)).isPresent())
            throw new IllegalStateException("An active return request already exists for this allocation");
        Employee requester=allocation.getEmployee();
        if (currentUserService.isAuthenticated() && "Employee".equalsIgnoreCase(currentUserService.getRoleName())) {
            if (requester == null || !requester.getId().equals(currentUserService.requireUserId())) {
                throw new org.springframework.security.access.AccessDeniedException("Employee can request return only for their own allocated asset");
            }
        }
        if(assetReturn.getRequestedBy()!=null && assetReturn.getRequestedBy().getId()!=null)
            requester=employeeRepository.findById(assetReturn.getRequestedBy().getId()).orElseThrow(()->new ResourceNotFoundException("Requested employee not found"));
        if(requester==null) throw new IllegalArgumentException("requestedBy is required");
        assetReturn.setAllocation(allocation); assetReturn.setRequestedBy(requester); assetReturn.setId(null);
        assetReturn.setStatus(ReturnStatus.REQUESTED);
        AssetReturn saved=assetReturnRepository.save(assetReturn);
        record(allocation.getAsset(),"RETURN_REQUESTED",null,null,"Asset return requested by "+requester.getFullName());
        return saved;
    }

    @Override @Transactional
    public AssetReturn update(UUID id, AssetReturn input) {
        AssetReturn existing=get(id);
        if(existing.getStatus()!=ReturnStatus.REQUESTED)
            throw new IllegalStateException("Only REQUESTED returns can be edited");
        if(input.getAllocation()!=null && input.getAllocation().getId()!=null)
            existing.setAllocation(allocationRepository.findById(input.getAllocation().getId()).orElseThrow(()->new ResourceNotFoundException("Allocation not found")));
        if(input.getRequestedBy()!=null && input.getRequestedBy().getId()!=null)
            existing.setRequestedBy(employeeRepository.findById(input.getRequestedBy().getId()).orElseThrow(()->new ResourceNotFoundException("Requested employee not found")));
        if(input.getReason()!=null) existing.setReason(input.getReason());
        return assetReturnRepository.save(existing);
    }

    @Override @Transactional
    public AssetReturn verify(UUID id, UUID verifierId){
        AssetReturn ret=get(id);
        if(ret.getStatus()!=ReturnStatus.REQUESTED) throw new IllegalStateException("Only REQUESTED returns can be verified");
        Employee verifier=employeeRepository.findById(verifierId).orElseThrow(()->new ResourceNotFoundException("Verifier not found"));
        ret.setVerifiedBy(verifier); ret.setVerifiedOn(LocalDate.now()); ret.setStatus(ReturnStatus.VERIFIED);
        Asset asset=ret.getAllocation().getAsset();
        asset.setStatus(AssetStatus.IN_REPAIR);
        record(asset,"RETURN_VERIFIED","status", "ASSIGNED","Return verified by "+verifier.getFullName());
        return assetReturnRepository.save(ret);
    }

    @Override @Transactional
    public AssetReturn complete(UUID id){
        AssetReturn ret=get(id);
        if(ret.getStatus()!=ReturnStatus.VERIFIED) throw new IllegalStateException("Only VERIFIED returns can be completed");
        ret.setStatus(ReturnStatus.COMPLETED);
        Asset asset=ret.getAllocation().getAsset();
        asset.setCurrentOwner(null); asset.setStatus(AssetStatus.AVAILABLE);
        asset.setCurrentLocation(asset.getBranch()!=null?asset.getBranch().getBranchName():null);
        record(asset,"RETURN_COMPLETED","status","IN_REPAIR","Asset returned and made available");
        return assetReturnRepository.save(ret);
    }

    @Override public void delete(UUID id){ if(!assetReturnRepository.existsById(id)) throw new ResourceNotFoundException("Asset Return not found"); assetReturnRepository.deleteById(id); }
    private AssetReturn get(UUID id){ return assetReturnRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Asset Return not found: "+id)); }
    private void record(Asset asset,String action,String field,String oldV,String description){
        historyService.recordEvent(asset.getId(), action, field, oldV,
                asset.getStatus()!=null?asset.getStatus().name():null, description, "SYSTEM");
    }
}
