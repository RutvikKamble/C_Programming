package com.example.assets_management.asset_master.service;
import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.asset_master.repository.AssetRepository;

import com.example.assets_management.asset_category.entity.AssetCategory;
import com.example.assets_management.asset_category.repository.AssetCategoryRepository;
import com.example.assets_management.vendor.entity.Vendor;
import com.example.assets_management.vendor.repository.VendorRepository;
import com.example.assets_management.branch.entity.Branch;
import com.example.assets_management.branch.repository.BranchRepository;
import com.example.assets_management.company.entity.Company;
import com.example.assets_management.company.repository.CompanyRepository;
import com.example.assets_management.department.entity.Department;
import com.example.assets_management.department.repository.DepartmentRepository;
import com.example.assets_management.employee.entity.Employee;
import com.example.assets_management.employee.repository.EmployeeRepository;
import com.example.assets_management.asset_model.entity.AssetModel;
import com.example.assets_management.asset_model.repository.AssetModelRepository;
import com.example.assets_management.asset_history.service.AssetHistoryService;
import com.example.assets_management.security.context.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import com.example.assets_management.asset_master.enums.AssetStatus;
import com.example.assets_management.asset_master.enums.AssetType;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;
    private final CompanyRepository companyRepository;
    private final BranchRepository branchRepository;
    private final DepartmentRepository departmentRepository;
    private final AssetCategoryRepository assetCategoryRepository;
    private final VendorRepository vendorRepository;
    private final EmployeeRepository employeeRepository;
    private final AssetModelRepository assetModelRepository;
    private final AssetHistoryService assetHistoryService;
    private final CurrentUserService currentUserService;

    @Override
    public List<Asset> findAll() {
        return currentUserService.isAuthenticated() && currentUserService.getCompanyId() != null
                ? assetRepository.findAllActiveByCompanyId(currentUserService.getCompanyId())
                : assetRepository.findAllActive();
    }

    @Override
    public Optional<Asset> findById(UUID id) {
        return currentUserService.isAuthenticated() && currentUserService.getCompanyId() != null
                ? assetRepository.findActiveByIdAndCompany(id, currentUserService.getCompanyId())
                : assetRepository.findActiveById(id);
    }

    @Override
    public Asset save(Asset asset) {

        if (asset.getCompany() == null || asset.getCompany().getId() == null) {
            if (currentUserService.isAuthenticated() && currentUserService.getCompanyId() != null) {
                Company c = companyRepository.findById(currentUserService.getCompanyId()).orElseThrow(() -> new RuntimeException("Current company not found"));
                asset.setCompany(c);
            } else { throw new RuntimeException("Company is required"); }
        }
        if (currentUserService.isAuthenticated() && !currentUserService.getCompanyId().equals(asset.getCompany().getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Asset belongs to another company");
        }

        Company company = companyRepository.findById(asset.getCompany().getId())
                .orElseThrow(() -> new RuntimeException("Company not found"));
        asset.setCompany(company);

        if (asset.getBranch() != null) {
            Branch branch = branchRepository.findById(asset.getBranch().getId())
                    .orElseThrow(() -> new RuntimeException("Branch not found"));
            asset.setBranch(branch);
        }

        if (asset.getDepartment() != null) {
            Department department = departmentRepository.findById(asset.getDepartment().getId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            asset.setDepartment(department);
        }

        AssetCategory category = assetCategoryRepository.findById(asset.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("Asset Category not found"));
        asset.setCategory(category);

        if (asset.getModel() != null) {
            AssetModel model = assetModelRepository.findById(asset.getModel().getId())
                    .orElseThrow(() -> new RuntimeException("Asset Model not found"));
            asset.setModel(model);
        }

        if (asset.getVendor() != null) {
            Vendor vendor = vendorRepository.findById(asset.getVendor().getId())
                    .orElseThrow(() -> new RuntimeException("Vendor not found"));
            asset.setVendor(vendor);
        }

        if (asset.getCurrentOwner() != null) {
            Employee employee = employeeRepository.findById(asset.getCurrentOwner().getId())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            asset.setCurrentOwner(employee);
        }

        asset.setId(null);

        Asset saved = assetRepository.save(asset);

        // Auto-generate barcode and QR code values for the new asset.
        if (saved.getBarcodeValue() == null || saved.getBarcodeValue().isBlank()) {
            saved.setBarcodeValue(saved.getAssetCode() != null
                    ? saved.getAssetCode()
                    : saved.getId().toString().substring(0, 12));
        }
        if (saved.getQrCodeValue() == null || saved.getQrCodeValue().isBlank()) {
            saved.setQrCodeValue("AST:" + saved.getAssetCode() + ":" + saved.getId());
        }

        // Record a CREATED history event for traceability.
        recordHistory(saved, "CREATED", null, null, "Asset created");

        return assetRepository.save(saved);
    }

    @Override
    public Asset update(UUID id, Asset asset) {

        Asset existing = findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found or outside company scope"));

        if (existing.getDeletedAt() != null) {
            throw new RuntimeException("Cannot update deleted asset");
        }

        if (asset.getCompany() != null) {
            Company company = companyRepository.findById(asset.getCompany().getId())
                    .orElseThrow(() -> new RuntimeException("Company not found"));
            existing.setCompany(company);
        }

        if (asset.getBranch() != null) {
            Branch branch = branchRepository.findById(asset.getBranch().getId())
                    .orElseThrow(() -> new RuntimeException("Branch not found"));
            existing.setBranch(branch);
        } else {
            existing.setBranch(null);
        }

        if (asset.getDepartment() != null) {
            Department department = departmentRepository.findById(asset.getDepartment().getId())
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            existing.setDepartment(department);
        } else {
            existing.setDepartment(null);
        }

        if (asset.getCategory() != null) {
            AssetCategory category = assetCategoryRepository.findById(asset.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            existing.setCategory(category);
        }

        if (asset.getModel() != null) {
            AssetModel model = assetModelRepository.findById(asset.getModel().getId())
                    .orElseThrow(() -> new RuntimeException("Asset Model not found"));
            existing.setModel(model);
        } else {
            existing.setModel(null);
        }

        if (asset.getVendor() != null) {
            Vendor vendor = vendorRepository.findById(asset.getVendor().getId())
                    .orElseThrow(() -> new RuntimeException("Vendor not found"));
            existing.setVendor(vendor);
        } else {
            existing.setVendor(null);
        }

        if (asset.getCurrentOwner() != null) {
            Employee employee = employeeRepository.findById(asset.getCurrentOwner().getId())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            existing.setCurrentOwner(employee);
        } else {
            existing.setCurrentOwner(null);
        }

        existing.setAssetCode(asset.getAssetCode());
        existing.setName(asset.getName());
        existing.setAssetType(asset.getAssetType());
        existing.setSerialNumber(asset.getSerialNumber());
        existing.setPurchaseDate(asset.getPurchaseDate());
        existing.setPurchaseCost(asset.getPurchaseCost());
        existing.setCurrentValue(asset.getCurrentValue());
        existing.setStatus(asset.getStatus());
        existing.setCurrentLocation(asset.getCurrentLocation());
        existing.setQrCodeValue(asset.getQrCodeValue());
        existing.setBarcodeValue(asset.getBarcodeValue());

        Asset updated = assetRepository.save(existing);

        // Record a UPDATED history event for traceability.
        recordHistory(updated, "UPDATED", null, null, "Asset updated");

        return updated;
    }

    @Override
    public void delete(UUID id) {
        Asset asset = findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found or outside company scope"));

        if (asset.getDeletedAt() != null) {
            throw new RuntimeException("Asset already deleted");
        }

        // Record DELETED history event
        recordHistory(asset, "DELETED", null, null, "Asset deleted");

        // Soft delete - set deletedAt instead of removing record
        asset.setDeletedAt(LocalDateTime.now());
        assetRepository.save(asset);
    }


    @Override
    public void bulkDelete(List<UUID> ids) {

        List<Asset> assets = assetRepository.findAllById(ids);

        if (assets.size() != ids.size()) {
            throw new RuntimeException("One or more Assets not found");
        }

        // Check if any assets are already deleted
        for (Asset asset : assets) {
            if (asset.getDeletedAt() != null) {
                throw new RuntimeException("Asset " + asset.getAssetCode() + " is already deleted");
            }
        }

        // Soft delete - set deletedAt for all assets
        LocalDateTime now = LocalDateTime.now();
        for (Asset asset : assets) {
            recordHistory(asset, "DELETED", null, null, "Asset deleted");
            asset.setDeletedAt(now);
        }
        assetRepository.saveAll(assets);

    }

    @Override
    public List<Asset> bulkUpdate(List<Asset> assets) {

        List<Asset> updatedAssets = new ArrayList<>();

        for (Asset asset : assets) {

            if (asset.getId() == null) {
                throw new RuntimeException("Asset ID is required");
            }

            updatedAssets.add(update(asset.getId(), asset));

        }

        return updatedAssets;

    }


    @Override
    @SuppressWarnings("unchecked")
    public Asset patch(UUID id, Map<String, Object> updates) {

        Asset asset = findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found or outside company scope"));

        if (asset.getDeletedAt() != null) {
            throw new RuntimeException("Cannot update deleted asset");
        }

        updates.forEach((key, value) -> {

            switch (key) {

                case "assetCode" ->
                        asset.setAssetCode((String) value);

                case "name" ->
                        asset.setName((String) value);

                case "serialNumber" ->
                        asset.setSerialNumber((String) value);

                case "purchaseDate" ->
                        asset.setPurchaseDate(LocalDate.parse(value.toString()));

                case "purchaseCost" ->
                        asset.setPurchaseCost(new BigDecimal(value.toString()));

                case "currentValue" ->
                        asset.setCurrentValue(new BigDecimal(value.toString()));

                case "assetType" ->
                        asset.setAssetType(AssetType.valueOf(value.toString()));

                case "status" ->
                        asset.setStatus(AssetStatus.valueOf(value.toString()));

                case "currentLocation" ->
                        asset.setCurrentLocation((String) value);

                case "qrCodeValue" ->
                        asset.setQrCodeValue((String) value);

                case "barcodeValue" ->
                        asset.setBarcodeValue((String) value);

                case "company" -> {

                    UUID companyId = UUID.fromString(
                            ((Map<String, Object>) value).get("id").toString());

                    Company company = companyRepository.findById(companyId)
                            .orElseThrow(() -> new RuntimeException("Company not found"));

                    asset.setCompany(company);

                }

                case "branch" -> {

                    UUID branchId = UUID.fromString(
                            ((Map<String, Object>) value).get("id").toString());

                    Branch branch = branchRepository.findById(branchId)
                            .orElseThrow(() -> new RuntimeException("Branch not found"));

                    asset.setBranch(branch);

                }

                case "department" -> {

                    UUID departmentId = UUID.fromString(
                            ((Map<String, Object>) value).get("id").toString());

                    Department department = departmentRepository.findById(departmentId)
                            .orElseThrow(() -> new RuntimeException("Department not found"));

                    asset.setDepartment(department);

                }

                case "category" -> {

                    UUID categoryId = UUID.fromString(
                            ((Map<String, Object>) value).get("id").toString());

                    AssetCategory category = assetCategoryRepository.findById(categoryId)
                            .orElseThrow(() -> new RuntimeException("Category not found"));

                    asset.setCategory(category);

                }

                case "model" -> {

                    UUID modelId = UUID.fromString(
                            ((Map<String, Object>) value).get("id").toString());

                    AssetModel model = assetModelRepository.findById(modelId)
                            .orElseThrow(() -> new RuntimeException("Asset Model not found"));

                    asset.setModel(model);

                }

                case "vendor" -> {

                    UUID vendorId = UUID.fromString(
                            ((Map<String, Object>) value).get("id").toString());

                    Vendor vendor = vendorRepository.findById(vendorId)
                            .orElseThrow(() -> new RuntimeException("Vendor not found"));

                    asset.setVendor(vendor);

                }

                case "currentOwner" -> {

                    UUID employeeId = UUID.fromString(
                            ((Map<String, Object>) value).get("id").toString());

                    Employee employee = employeeRepository.findById(employeeId)
                            .orElseThrow(() -> new RuntimeException("Employee not found"));

                    asset.setCurrentOwner(employee);

                }

            }

        });

        return assetRepository.save(asset);

    }

    @Override
    public void importAssets(MultipartFile file) throws IOException {

        Workbook workbook = new XSSFWorkbook(file.getInputStream());

        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {

            Row row = sheet.getRow(i);

            if (row == null) {
                continue;
            }

            Asset asset = new Asset();

            asset.setAssetCode(row.getCell(0).getStringCellValue());
            asset.setName(row.getCell(1).getStringCellValue());

            if (row.getCell(2) != null) {
                asset.setSerialNumber(row.getCell(2).getStringCellValue());
            }

            if (row.getCell(3) != null) {
                asset.setPurchaseCost(
                        BigDecimal.valueOf(row.getCell(3).getNumericCellValue()));
            }

            if (row.getCell(4) != null) {
                asset.setCurrentValue(
                        BigDecimal.valueOf(row.getCell(4).getNumericCellValue()));
            }

            if (row.getCell(5) != null) {
                asset.setStatus(
                        AssetStatus.valueOf(row.getCell(5).getStringCellValue()));
            }

            assetRepository.save(asset);

        }

        workbook.close();

    }

    @Override
    public void exportAssets(HttpServletResponse response) throws IOException {

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Assets");

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("Asset Code");
        header.createCell(1).setCellValue("Asset Name");
        header.createCell(2).setCellValue("Serial Number");
        header.createCell(3).setCellValue("Purchase Cost");
        header.createCell(4).setCellValue("Current Value");
        header.createCell(5).setCellValue("Status");

        List<Asset> assets = assetRepository.findAll();

        int rowNum = 1;

        for (Asset asset : assets) {

            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(asset.getAssetCode());

            row.createCell(1).setCellValue(asset.getName());

            row.createCell(2).setCellValue(
                    asset.getSerialNumber() == null ? "" : asset.getSerialNumber());

            row.createCell(3).setCellValue(
                    asset.getPurchaseCost() == null
                            ? 0
                            : asset.getPurchaseCost().doubleValue());

            row.createCell(4).setCellValue(
                    asset.getCurrentValue() == null
                            ? 0
                            : asset.getCurrentValue().doubleValue());

            row.createCell(5).setCellValue(
                    asset.getStatus() == null
                            ? ""
                            : asset.getStatus().name());

        }

        workbook.write(response.getOutputStream());

        workbook.close();

    }

    /**
     * Records a history entry for an asset using the AssetHistory service.
     */
    private void recordHistory(Asset asset, String action, String fieldChanged,
                               String newValue, String description) {
        assetHistoryService.recordEvent(
                asset.getId(), action, fieldChanged, null, newValue, description, "SYSTEM");
    }

}
