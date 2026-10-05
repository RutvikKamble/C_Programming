package com.example.assets_management.asset_allocation.service;
import com.example.assets_management.asset_allocation.entity.Allocation;
import com.example.assets_management.asset_allocation.repository.AllocationRepository;
import com.example.assets_management.asset_allocation.dto.response.AllocationResponse;

import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.asset_master.repository.AssetRepository;
import com.example.assets_management.branch.entity.Branch;
import com.example.assets_management.branch.repository.BranchRepository;
import com.example.assets_management.department.entity.Department;
import com.example.assets_management.department.repository.DepartmentRepository;
import com.example.assets_management.employee.entity.Employee;
import com.example.assets_management.employee.repository.EmployeeRepository;
import com.example.assets_management.asset_return.repository.AssetReturnRepository;
import com.example.assets_management.asset_return.entity.AssetReturn;
import com.example.assets_management.asset_return.enums.ReturnStatus;
import com.example.assets_management.asset_history.service.AssetHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.assets_management.asset_allocation.dto.request.AllocateAssetRequest;
import com.example.assets_management.asset_allocation.dto.request.DeallocateAssetRequest;
import com.example.assets_management.asset_allocation.enums.AllocationStatus;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AllocationServiceImpl implements AllocationService {

    private final AllocationRepository allocationRepository;

    private final AssetRepository assetRepository;

    private final EmployeeRepository employeeRepository;

    private final BranchRepository branchRepository;

    private final DepartmentRepository departmentRepository;

    private final AssetReturnRepository assetReturnRepository;

    private final AssetHistoryService assetHistoryService;

    // ================= FIND ALL =================

    @Override
    public List<AllocationResponse> findAll() {
        List<Allocation> allocations = allocationRepository.findAll();
        List<AllocationResponse> responses = new ArrayList<>();

        for (Allocation allocation : allocations) {
            AllocationResponse response = new AllocationResponse();
            response.setAllocationId(allocation.getId());
            response.setAssetId(allocation.getAsset().getId());
            response.setAssetCode(allocation.getAsset().getAssetCode());
            response.setAssetName(allocation.getAsset().getName());
            response.setEmployeeId(allocation.getEmployee().getId());
            response.setEmployeeName(allocation.getEmployee().getFullName());
            response.setDepartment(allocation.getDepartment() != null ? allocation.getDepartment().getDepartmentName() : null);
            response.setBranch(allocation.getBranch() != null ? allocation.getBranch().getBranchName() : null);
            response.setAllocatedOn(allocation.getAllocatedOn());
            response.setReturnedOn(allocation.getReturnedOn());
            response.setStatus(allocation.getStatus());
            response.setAcknowledgedByEmployee(allocation.getAcknowledgedByEmployee());
            responses.add(response);
        }

        return responses;
    }

    // ================= FIND BY ID =================

    @Override
    public Optional<Allocation> findById(UUID id) {

        return allocationRepository.findById(id);

    }

    // ================= SAVE =================

    @Override
    public Allocation save(Allocation allocation) {

        Asset asset = assetRepository.findById(
                        allocation.getAsset().getId())
                .orElseThrow(() ->
                        new RuntimeException("Asset not found"));

        allocation.setAsset(asset);

        Employee employee = employeeRepository.findById(
                        allocation.getEmployee().getId())
                .orElseThrow(() ->
                        new RuntimeException("Employee not found"));

        allocation.setEmployee(employee);

        if (allocation.getBranch() != null) {

            Branch branch = branchRepository.findById(
                            allocation.getBranch().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Branch not found"));

            allocation.setBranch(branch);

        }

        if (allocation.getDepartment() != null) {

            Department department = departmentRepository.findById(
                            allocation.getDepartment().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Department not found"));

            allocation.setDepartment(department);

        }

        allocation.setId(null);

        return allocationRepository.save(allocation);

    }

    // ================= UPDATE =================

    @Override
    public Allocation update(UUID id, Allocation allocation) {

        Allocation existing = allocationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Allocation not found"));

        if (allocation.getAsset() != null) {

            Asset asset = assetRepository.findById(
                            allocation.getAsset().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Asset not found"));

            existing.setAsset(asset);

        }

        if (allocation.getEmployee() != null) {

            Employee employee = employeeRepository.findById(
                            allocation.getEmployee().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Employee not found"));

            existing.setEmployee(employee);

        }

        if (allocation.getBranch() != null) {

            Branch branch = branchRepository.findById(
                            allocation.getBranch().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Branch not found"));

            existing.setBranch(branch);

        } else {

            existing.setBranch(null);

        }

        if (allocation.getDepartment() != null) {

            Department department = departmentRepository.findById(
                            allocation.getDepartment().getId())
                    .orElseThrow(() ->
                            new RuntimeException("Department not found"));

            existing.setDepartment(department);

        } else {

            existing.setDepartment(null);

        }

        existing.setAllocatedOn(allocation.getAllocatedOn());

        existing.setReturnedOn(allocation.getReturnedOn());

        existing.setStatus(allocation.getStatus());

        existing.setAcknowledgedByEmployee(
                allocation.getAcknowledgedByEmployee());

        return allocationRepository.save(existing);

    }

    // ================= DELETE =================

    @Override
    public void delete(UUID id) {

        // Check if allocation exists
        Allocation allocation = allocationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Allocation not found"));

        // Check if there are any AssetReturn records referencing this allocation
        boolean hasRelatedReturns = assetReturnRepository.existsByAllocationId(id);
        if (hasRelatedReturns) {
            throw new RuntimeException("Cannot delete allocation: it has related AssetReturn records. Delete the AssetReturn records first.");
        }

        allocationRepository.deleteById(id);

    }

    // ======================================================
    // Remaining methods
    // (We'll implement one by one)
    // ======================================================

    @Override
    public Allocation allocate(AllocateAssetRequest request) {

        // Validate Asset
        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        // Only AVAILABLE assets may be allocated.
        if (asset.getStatus() != null && asset.getStatus() != com.example.assets_management.asset_master.enums.AssetStatus.AVAILABLE) {
            throw new IllegalStateException("Only AVAILABLE assets can be allocated. Current status: " + asset.getStatus());
        }

        // Check if already allocated
        allocationRepository.findByAssetIdAndStatus(
                asset.getId(),
                AllocationStatus.ALLOCATED
        ).ifPresent(a -> {
            throw new RuntimeException("Asset is already allocated.");
        });

        // Validate Employee
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Allocation allocation = new Allocation();

        allocation.setAsset(asset);

        allocation.setEmployee(employee);

        // Copy Employee Branch
        allocation.setBranch(employee.getBranch());

        // Copy Employee Department
        allocation.setDepartment(employee.getDepartment());

        allocation.setAllocatedOn(request.getAllocatedOn());

        allocation.setReturnedOn(null);

        allocation.setStatus(AllocationStatus.ALLOCATED);

        allocation.setAcknowledgedByEmployee(
                request.getAcknowledgedByEmployee()
        );

        // Update Asset

        asset.setCurrentOwner(employee);

        asset.setBranch(employee.getBranch());

        asset.setDepartment(employee.getDepartment());

        asset.setCurrentLocation(
                employee.getBranch() != null
                        ? employee.getBranch().getBranchName()
                        : null
        );

        asset.setStatus(
                com.example.assets_management.asset_master.enums.AssetStatus.ASSIGNED
        );

        assetRepository.save(asset);
        recordHistory(asset, "ALLOCATED", "status", "AVAILABLE", "Asset allocated to " + employee.getFullName());

        return allocationRepository.save(allocation);

    }

    @Override
    public Allocation deallocate(DeallocateAssetRequest request) {


        Allocation allocation = allocationRepository.findById(request.getAllocationId())
                .orElseThrow(() -> new RuntimeException("Allocation not found"));

        /*
        Allocation allocation = allocationRepository
                .findByAssetIdAndStatus(
                        assetId,
                        AllocationStatus.ALLOCATED
                )
                .orElseThrow(() ->
                        new RuntimeException("Active allocation not found"));
        */

        if (allocation.getStatus() == AllocationStatus.RETURNED) {
            throw new RuntimeException("Asset is already deallocated.");
        }

        // A normal deallocation is only allowed after a verified return has been completed.
        AssetReturn latestReturn = assetReturnRepository.findByAllocationId(allocation.getId()).stream()
                .max(java.util.Comparator.comparing(AssetReturn::getId))
                .orElse(null);
        if (latestReturn == null || latestReturn.getStatus() != ReturnStatus.COMPLETED) {
            throw new IllegalStateException("Return must be verified and completed before deallocation.");
        }

        Asset asset = allocation.getAsset();

        // Update Allocation

        allocation.setReturnedOn(request.getReturnedOn());

        allocation.setStatus(AllocationStatus.RETURNED);

        allocation.setAcknowledgedByEmployee(Boolean.TRUE);

        // Update Asset

        asset.setCurrentOwner(null);

        asset.setStatus(
                com.example.assets_management.asset_master.enums.AssetStatus.AVAILABLE
        );

        assetRepository.save(asset);
        recordHistory(asset, "DEALLOCATED", "status", "ASSIGNED", "Asset deallocated after completed return");

        return allocationRepository.save(allocation);

    }

    @Override
    public Allocation getCurrentAllocation(UUID assetId) {

        return allocationRepository
                .findByAssetIdAndStatus(
                        assetId,
                        AllocationStatus.ALLOCATED
                )
                .orElseThrow(() ->
                        new RuntimeException("No active allocation found."));

    }

    @Override
    public List<Allocation> getHistory(UUID assetId) {
        return allocationRepository
                .findByAssetIdOrderByAllocatedOnDesc(assetId);
    }

    @Override
    public List<Allocation> getByCategory(UUID categoryId) {

        return allocationRepository.findByAsset_Category_Id(categoryId);
    }


    @Override
    public Allocation getAllocationDetails(UUID allocationId) {

        return allocationRepository.findById(allocationId)
                .orElseThrow(() ->
                        new RuntimeException("Allocation not found"));
    }


    @Override
    @SuppressWarnings("unchecked")
    public Allocation patch(UUID id, Map<String, Object> updates) {

        Allocation allocation = allocationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Allocation not found"));

        updates.forEach((key, value) -> {

            switch (key) {

                case "allocatedOn" ->
                        allocation.setAllocatedOn(LocalDate.parse(value.toString()));

                case "returnedOn" ->
                        allocation.setReturnedOn(LocalDate.parse(value.toString()));

                case "acknowledgedByEmployee" ->
                        allocation.setAcknowledgedByEmployee(
                                Boolean.parseBoolean(value.toString()));

                case "status" ->
                        allocation.setStatus(
                                AllocationStatus.valueOf(value.toString()));

                case "asset" -> {

                    UUID assetId = UUID.fromString(
                            ((Map<String,Object>) value).get("id").toString());

                    Asset asset = assetRepository.findById(assetId)
                            .orElseThrow(() ->
                                    new RuntimeException("Asset not found"));

                    allocation.setAsset(asset);

                }

                case "employee" -> {

                    UUID employeeId = UUID.fromString(
                            ((Map<String,Object>) value).get("id").toString());

                    Employee employee = employeeRepository.findById(employeeId)
                            .orElseThrow(() ->
                                    new RuntimeException("Employee not found"));

                    allocation.setEmployee(employee);

                    allocation.setBranch(employee.getBranch());

                    allocation.setDepartment(employee.getDepartment());

                }

            }

        });

        return allocationRepository.save(allocation);

    }

    @Override
    public void bulkDelete(List<UUID> ids) {

        List<Allocation> allocations =
                allocationRepository.findAllById(ids);

        if (allocations.size() != ids.size()) {

            throw new RuntimeException(
                    "One or more Allocation IDs not found");

        }

        allocationRepository.deleteAll(allocations);

    }

    @Override
    public List<Allocation> bulkUpdate(List<Allocation> allocations) {

        List<Allocation> updated = new ArrayList<>();

        for (Allocation allocation : allocations) {

            if (allocation.getId() == null) {

                throw new RuntimeException(
                        "Allocation ID is required");

            }

            updated.add(
                    update(
                            allocation.getId(),
                            allocation
                    )
            );

        }

        return updated;

    }


    /*
    @Override
    public void importAllocation(MultipartFile file)
            throws IOException {

        Workbook workbook =
                new XSSFWorkbook(file.getInputStream());

        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {

            Row row = sheet.getRow(i);

            if (row == null)
                continue;

            Allocation allocation = new Allocation();

            UUID assetId =
                    UUID.fromString(row.getCell(0).getStringCellValue());

            UUID employeeId =
                    UUID.fromString(row.getCell(1).getStringCellValue());

            Asset asset = assetRepository.findById(assetId)
                    .orElseThrow(() ->
                            new RuntimeException("Asset not found"));

            Employee employee =
                    employeeRepository.findById(employeeId)
                            .orElseThrow(() ->
                                    new RuntimeException("Employee not found"));

            allocation.setAsset(asset);

            allocation.setEmployee(employee);

            allocation.setBranch(employee.getBranch());

            allocation.setDepartment(employee.getDepartment());

            allocation.setAllocatedOn(
                    row.getCell(2).getLocalDateTimeCellValue()
                            .toLocalDate());

            allocation.setStatus(
                    AllocationStatus.valueOf(
                            row.getCell(3).getStringCellValue()));

            allocation.setAcknowledgedByEmployee(
                    row.getCell(4).getBooleanCellValue());

            allocationRepository.save(allocation);

        }

        workbook.close();

    }

    @Override
    public void exportAllocation(HttpServletResponse response)
            throws IOException {

        Workbook workbook =
                new XSSFWorkbook();

        Sheet sheet =
                workbook.createSheet("Allocations");

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("Asset Code");
        header.createCell(1).setCellValue("Employee");
        header.createCell(2).setCellValue("Branch");
        header.createCell(3).setCellValue("Department");
        header.createCell(4).setCellValue("Allocated On");
        header.createCell(5).setCellValue("Returned On");
        header.createCell(6).setCellValue("Status");

        int rowNum = 1;

        for (Allocation allocation :
                allocationRepository.findAll()) {

            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(
                    allocation.getAsset().getAssetCode());

            row.createCell(1).setCellValue(
                    allocation.getEmployee().getFullName());

            row.createCell(2).setCellValue(
                    allocation.getBranch() != null
                            ? allocation.getBranch().getBranchName()
                            : "");

            row.createCell(3).setCellValue(
                    allocation.getDepartment() != null
                            ? allocation.getDepartment().getDepartmentName()
                            : "");

            row.createCell(4).setCellValue(
                    allocation.getAllocatedOn().toString());

            row.createCell(5).setCellValue(
                    allocation.getReturnedOn() == null
                            ? ""
                            : allocation.getReturnedOn().toString());

            row.createCell(6).setCellValue(
                    allocation.getStatus().name());

        }

        workbook.write(response.getOutputStream());

        workbook.close();
    }
    */

    private void recordHistory(Asset asset, String action, String field, String oldValue, String description) {
        assetHistoryService.recordEvent(asset.getId(), action, field, oldValue,
                asset.getStatus() != null ? asset.getStatus().name() : null, description, "SYSTEM");
    }

}
