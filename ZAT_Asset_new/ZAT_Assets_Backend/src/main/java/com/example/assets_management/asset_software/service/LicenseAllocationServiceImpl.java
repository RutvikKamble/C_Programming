package com.example.assets_management.asset_software.service;

import com.example.assets_management.asset_history.dto.request.AssetHistoryRequest;
import com.example.assets_management.asset_history.service.AssetHistoryService;
import com.example.assets_management.asset_master.enums.AssetStatus;
import com.example.assets_management.asset_software.dto.request.LicenseAllocationRequest;
import com.example.assets_management.asset_software.dto.request.BulkLicenseAllocationRequest;
import com.example.assets_management.asset_software.dto.request.LicenseAllocationUpdateRequest;
import com.example.assets_management.asset_software.dto.response.LicenseAllocationResponse;
import com.example.assets_management.asset_software.entity.LicenseAllocation;
import com.example.assets_management.asset_software.entity.LicenseAllocation.AllocationStatus;
import com.example.assets_management.asset_software.entity.SoftwareLicense;
import com.example.assets_management.asset_software.enums.SoftwareLicenseStatus;
import com.example.assets_management.asset_software.repository.LicenseAllocationRepository;
import com.example.assets_management.asset_software.repository.SoftwareLicenseRepository;
import com.example.assets_management.employee.entity.Employee;
import com.example.assets_management.employee.enums.UserStatus;
import com.example.assets_management.employee.repository.EmployeeRepository;
import com.example.assets_management.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class LicenseAllocationServiceImpl implements LicenseAllocationService {

    private final SoftwareLicenseRepository licenseRepository;
    private final LicenseAllocationRepository allocationRepository;
    private final EmployeeRepository employeeRepository;
    private final AssetHistoryService historyService;

    public LicenseAllocationServiceImpl(SoftwareLicenseRepository licenseRepository,
            LicenseAllocationRepository allocationRepository,
            EmployeeRepository employeeRepository,
            AssetHistoryService historyService) {
        this.licenseRepository = licenseRepository;
        this.allocationRepository = allocationRepository;
        this.employeeRepository = employeeRepository;
        this.historyService = historyService;
    }

    @Override
    @Transactional
    public List<LicenseAllocationResponse> bulkAllocate(UUID licenseId, BulkLicenseAllocationRequest request) {
        SoftwareLicense license = lockedLicense(licenseId);
        ensureAllocatable(license);
        int available = license.getSeatCount() - license.getSeatsUsed();
        if (request.employeeIds().size() > available)
            throw new IllegalStateException("Bulk allocation exceeds available seats: " + available);
        List<LicenseAllocationResponse> result = new java.util.ArrayList<>();
        for (UUID employeeId : request.employeeIds()) {
            result.add(allocateWithinTransaction(license, employeeId, request.changedBy()));
        }
        return result;
    }

    private LicenseAllocationResponse allocateWithinTransaction(SoftwareLicense license, UUID employeeId,
            String changedBy) {
        Employee employee = activeEmployee(employeeId);
        ensureEmployeeCompanyMatchesAsset(employee, license);
        if (allocationRepository.existsBySoftwareLicense_IdAndEmployee_IdAndStatus(license.getId(), employee.getId(),
                AllocationStatus.ACTIVE))
            throw new IllegalStateException(
                    "Employee already has an active allocation for this license: " + employee.getId());
        if (license.getSeatsUsed() >= license.getSeatCount())
            throw new IllegalStateException("No available license seats");
        LicenseAllocation allocation = new LicenseAllocation();
        allocation.setSoftwareLicense(license);
        allocation.setEmployee(employee);
        allocation.setAllocatedDate(LocalDate.now());
        allocation.setStatus(AllocationStatus.ACTIVE);
        LicenseAllocation saved = allocationRepository.save(allocation);
        license.setSeatsUsed(license.getSeatsUsed() + 1);
        licenseRepository.save(license);
        historyService.create(new AssetHistoryRequest(license.getAsset().getId(), "LICENSE_SEAT_ALLOCATED", null, null,
                employee.getId().toString(), "Software license seat allocated to " + employee.getFullName(),
                changedBy));
        return toResponse(saved);
    }

    @Override
    @Transactional
    public LicenseAllocationResponse allocate(UUID licenseId, LicenseAllocationRequest request) {
        SoftwareLicense license = lockedLicense(licenseId);
        ensureAllocatable(license);
        return allocateWithinTransaction(license, request.employeeId(), request.changedBy());
    }

    @Override
    @Transactional
    public int bulkRelease(UUID licenseId, String changedBy) {
        SoftwareLicense license = lockedLicense(licenseId);
        List<LicenseAllocation> active = allocationRepository
                .findBySoftwareLicense_IdOrderByAllocatedDateDesc(licenseId).stream()
                .filter(a -> a.getStatus() == AllocationStatus.ACTIVE).toList();
        for (LicenseAllocation a : active) {
            a.setStatus(AllocationStatus.RELEASED);
            a.setReleasedDate(LocalDate.now());
            allocationRepository.save(a);
            historyService.create(new AssetHistoryRequest(license.getAsset().getId(), "LICENSE_SEAT_RELEASED", null,
                    a.getEmployee().getId().toString(), null,
                    "Bulk software license seat released from " + a.getEmployee().getFullName(), changedBy));
        }
        license.setSeatsUsed(0);
        licenseRepository.save(license);
        return active.size();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<LicenseAllocationResponse> findByLicense(UUID licenseId) {
        getLicense(licenseId);
        return allocationRepository.findBySoftwareLicense_IdOrderByAllocatedDateDesc(licenseId).stream()
                .map(this::toResponse).toList();
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public LicenseAllocationResponse findById(UUID licenseId, UUID allocationId) {
        getLicense(licenseId);
        return toResponse(getAllocationForLicense(licenseId, allocationId));
    }

    @Override
    @Transactional
    public LicenseAllocationResponse update(UUID licenseId, UUID allocationId, LicenseAllocationUpdateRequest request) {
        SoftwareLicense license = lockedLicense(licenseId);
        ensureAllocatable(license);
        LicenseAllocation allocation = getAllocationForLicense(licenseId, allocationId);
        if (allocation.getStatus() != AllocationStatus.ACTIVE) {
            throw new IllegalStateException("Released allocation cannot be updated");
        }
        if (!Boolean.TRUE.equals(license.getReassignable())) {
            throw new IllegalStateException("This software license is not reassignable");
        }
        Employee employee = activeEmployee(request.employeeId());
        ensureEmployeeCompanyMatchesAsset(employee, license);
        if (!allocation.getEmployee().getId().equals(employee.getId()) &&
                allocationRepository.existsBySoftwareLicense_IdAndEmployee_IdAndStatus(licenseId, employee.getId(),
                        AllocationStatus.ACTIVE)) {
            throw new IllegalStateException("Employee already has an active allocation for this license");
        }
        String oldEmployee = allocation.getEmployee().getId().toString();
        allocation.setEmployee(employee);
        LicenseAllocation saved = allocationRepository.save(allocation);
        historyService.create(new AssetHistoryRequest(license.getAsset().getId(), "LICENSE_ALLOCATION_UPDATED",
                "employeeId",
                oldEmployee, employee.getId().toString(), "Software license seat reassigned", request.changedBy()));
        return toResponse(saved);
    }

    @Override
    @Transactional
    public LicenseAllocationResponse release(UUID licenseId, UUID allocationId, String changedBy) {
        SoftwareLicense license = lockedLicense(licenseId);
        LicenseAllocation allocation = getAllocationForLicense(licenseId, allocationId);
        if (allocation.getStatus() != AllocationStatus.ACTIVE) {
            throw new IllegalStateException("License allocation is already released");
        }

        allocation.setStatus(AllocationStatus.RELEASED);
        allocation.setReleasedDate(LocalDate.now());
        LicenseAllocation saved = allocationRepository.save(allocation);

        license.setSeatsUsed(Math.max(0, license.getSeatsUsed() - 1));
        licenseRepository.save(license);

        historyService.create(new AssetHistoryRequest(license.getAsset().getId(), "LICENSE_SEAT_RELEASED", null,
                allocation.getEmployee().getId().toString(), null,
                "Software license seat released from " + allocation.getEmployee().getFullName(), changedBy));
        return toResponse(saved);
    }

    private SoftwareLicense lockedLicense(UUID id) {
        return licenseRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Software license not found: " + id));
    }

    private SoftwareLicense getLicense(UUID id) {
        return licenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Software license not found: " + id));
    }

    private LicenseAllocation getAllocationForLicense(UUID licenseId, UUID allocationId) {
        LicenseAllocation allocation = allocationRepository.findById(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("License allocation not found: " + allocationId));
        if (!allocation.getSoftwareLicense().getId().equals(licenseId)) {
            throw new IllegalArgumentException("Allocation does not belong to the requested software license");
        }
        return allocation;
    }

    private Employee activeEmployee(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
        if (employee.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalStateException("Employee must be ACTIVE to receive a software license");
        }
        return employee;
    }

    private void ensureEmployeeCompanyMatchesAsset(Employee employee, SoftwareLicense license) {
        if (!employee.getCompany().getId().equals(license.getAsset().getCompany().getId())) {
            throw new IllegalArgumentException("Employee and software asset must belong to the same company");
        }
    }

    private void ensureAllocatable(SoftwareLicense license) {
        if (license.getAsset().getStatus() == AssetStatus.CLOSED) {
            throw new IllegalStateException("Closed asset cannot receive software license allocations");
        }
        if (license.getStatus() == SoftwareLicenseStatus.CANCELLED) {
            throw new IllegalStateException("Cancelled software license cannot be allocated");
        }
        if (license.getExpiryDate().isBefore(LocalDate.now())) {
            license.setStatus(SoftwareLicenseStatus.EXPIRED);
            licenseRepository.save(license);
            throw new IllegalStateException("Expired software license cannot be allocated");
        }
    }

    private LicenseAllocationResponse toResponse(LicenseAllocation a) {
        Employee e = a.getEmployee();
        return new LicenseAllocationResponse(a.getId(), a.getSoftwareLicense().getId(), e.getId(), e.getEmployeeCode(),
                e.getFullName(), e.getEmail(), a.getAllocatedDate(), a.getReleasedDate(), a.getStatus());
    }
}
