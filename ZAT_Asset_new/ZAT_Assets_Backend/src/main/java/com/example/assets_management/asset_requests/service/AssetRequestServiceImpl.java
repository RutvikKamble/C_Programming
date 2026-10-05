package com.example.assets_management.asset_requests.service;

import com.example.assets_management.asset_allocation.dto.request.AllocateAssetRequest;
import com.example.assets_management.asset_allocation.service.AllocationService;
import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.asset_master.enums.AssetStatus;
import com.example.assets_management.asset_master.repository.AssetRepository;
import com.example.assets_management.asset_master.service.AssetService;
import com.example.assets_management.asset_category.entity.AssetCategory;
import com.example.assets_management.asset_model.entity.AssetModel;
import com.example.assets_management.asset_requests.entity.AssetRequest;
import com.example.assets_management.asset_requests.enums.AssetRequestPriority;
import com.example.assets_management.asset_requests.enums.AssetRequestStatus;
import com.example.assets_management.asset_requests.enums.AssetRequestType;
import com.example.assets_management.asset_requests.repository.AssetRequestRepository;
import com.example.assets_management.asset_transfer.dto.TransferRequest;
import com.example.assets_management.asset_transfer.service.AssetTransferService;
import com.example.assets_management.branch.entity.Branch;
import com.example.assets_management.branch.repository.BranchRepository;
import com.example.assets_management.company.entity.Company;
import com.example.assets_management.company.repository.CompanyRepository;
import com.example.assets_management.department.entity.Department;
import com.example.assets_management.security.context.CurrentUserService;
import com.example.assets_management.employee.entity.Employee;
import com.example.assets_management.employee.repository.EmployeeRepository;
import com.example.assets_management.exception.ResourceNotFoundException;
import com.example.assets_management.banking.transaction.service.BankingTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssetRequestServiceImpl implements AssetRequestService {

    private final AssetRequestRepository repo;
    private final AllocationService allocationService;
    private final EmployeeRepository employeeRepository;
    private final CurrentUserService currentUserService;
    private final AssetRepository assetRepository;
    private final AssetService assetService;
    private final AssetTransferService assetTransferService;
    private final BankingTransactionService bankingTransactionService;
    private final BranchRepository branchRepository;
    private final CompanyRepository companyRepository;

    @Override
    @Transactional
    public AssetRequest createRequest(AssetRequest r) {
        if (r.getRequestType() == null) throw new IllegalArgumentException("requestType is required");
        if (r.getPriority() == null) r.setPriority(AssetRequestPriority.MEDIUM);
        if (r.getRequestDate() == null) r.setRequestDate(LocalDate.now());

        if (isNewWorkflow(r.getRequestType())) {
            if (r.getStatus() == null) r.setStatus(AssetRequestStatus.DRAFT);
            if (r.getStatus() != AssetRequestStatus.DRAFT) {
                throw new IllegalStateException("New workflow requests must start in DRAFT");
            }

            validateNewWorkflowRequest(r);
            captureAssetTemplate(r);
            normalizePurchaseAmounts(r);
            resolveReferences(r);

            // IMPORTANT: the nested Asset in the incoming JSON is only a template.
            // Do not persist it here. The real Asset Master records are created later.
            r.setAsset(null);
            r.setAssets(new ArrayList<>());
        } else {
            if (r.getStatus() == null) r.setStatus(AssetRequestStatus.SEND);
        }

        return repo.save(r);
    }

    @Override
    @Transactional
    public AssetRequest updateRequest(Long id, AssetRequest r) {
        AssetRequest e = getRequestById(id);
        if (isNewWorkflow(e.getRequestType()) && e.getStatus() != AssetRequestStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT workflow requests can be updated");
        }

        e.setRequestType(r.getRequestType());
        e.setRequestedCategory(r.getRequestedCategory());
        e.setEmployeeId(r.getEmployeeId());
        e.setEmployeeName(r.getEmployeeName());
        e.setDepartment(r.getDepartment());
        e.setRequestedBy(r.getRequestedBy());
        e.setRequestDate(r.getRequestDate() == null ? e.getRequestDate() : r.getRequestDate());
        e.setPriority(r.getPriority() == null ? e.getPriority() : r.getPriority());
        e.setSourceCompany(r.getSourceCompany());
        e.setSourceBranch(r.getSourceBranch());
        e.setDestinationCompany(r.getDestinationCompany());
        e.setDestinationBranch(r.getDestinationBranch());
        e.setVendor(r.getVendor());
        e.setPurchaseCost(r.getPurchaseCost());
        e.setInvoiceNumber(r.getInvoiceNumber());
        e.setInvoiceAmount(r.getInvoiceAmount());
        e.setQuantity(r.getQuantity());
        e.setRemarks(r.getRemarks());

        if (isNewWorkflow(e.getRequestType())) {
            validateNewWorkflowRequest(r);
            copyAssetTemplate(r, e);
            normalizePurchaseAmounts(e);
            resolveReferences(e);

            // Never attach the incoming transient Asset to the request.
            e.setAsset(null);
            e.setAssets(new ArrayList<>());
        } else {
            // Legacy requests may still reference an existing persisted Asset.
            e.setAsset(resolveExistingAsset(r.getAsset()));
            resolveReferences(e);
        }

        return repo.save(e);
    }

    @Override
    public void deleteRequest(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("AssetRequest not found with id: " + id);
        repo.deleteById(id);
    }

    @Override
    public AssetRequest getRequestById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("AssetRequest not found with id: " + id));
    }

    private AssetRequest getRequestForUpdate(Long id) {
        return repo.findByIdForUpdate(id).orElseThrow(() -> new ResourceNotFoundException("AssetRequest not found with id: " + id));
    }

    @Override public List<AssetRequest> getAllRequests() { return repo.findAll(); }
    @Override public List<AssetRequest> getRequestsByAssetId(UUID id) { return repo.findByAnyAssetId(id); }
    @Override public List<AssetRequest> getRequestsByStatus(String status) { return repo.findByStatus(AssetRequestStatus.valueOf(status.toUpperCase())); }

    @Override
    @Transactional
    public AssetRequest send(Long id, String by) {
        AssetRequest r = getRequestForUpdate(id);
        requireNewWorkflow(r);
        requireStatus(r, AssetRequestStatus.DRAFT);
        r.setStatus(AssetRequestStatus.SENT);
        addRemark(r, "Sent by: " + by);
        return repo.save(r);
    }

    @Override
    @Transactional
    public AssetRequest receive(Long id, String by) {
        AssetRequest r = getRequestForUpdate(id);
        requireNewWorkflow(r);
        requireStatus(r, AssetRequestStatus.SENT);
        r.setReceivedBy(by);
        r.setReceivedDate(LocalDateTime.now());

        if (r.getRequestType() == AssetRequestType.CREATE) {
            List<Asset> createdAssets = createAssetsFromRequest(r);
            setCreatedAssets(r, createdAssets);
            r.setStatus(AssetRequestStatus.RECEIVED);
        } else if (r.getRequestType() == AssetRequestType.PURCHASE) {
            // Receiving the physical purchase is separate from Asset Master creation and payment.
            r.setStatus(AssetRequestStatus.RECEIVED_ASSET);
        } else {
            validateTransferRequest(r);
            r.setStatus(AssetRequestStatus.RECEIVED);
        }
        return repo.save(r);
    }

    @Override
    @Transactional
    public AssetRequest createAsset(Long id, String by) {
        AssetRequest r = getRequestForUpdate(id);
        requireNewWorkflow(r);
        if (r.getRequestType() != AssetRequestType.PURCHASE) {
            throw new IllegalStateException("CREATE_ASSET is only valid for PURCHASE requests");
        }
        requireStatus(r, AssetRequestStatus.RECEIVED_ASSET);

        normalizePurchaseAmounts(r);
        List<Asset> createdAssets = createAssetsFromRequest(r);
        setCreatedAssets(r, createdAssets);
        r.setStatus(AssetRequestStatus.PAY_BILL);
        addRemark(r, createdAssets.size() + " Asset Master record(s) created by: " + by);
        return repo.save(r);
    }

    @Override
    @Transactional
    public AssetRequest approve(Long id, String by) {
        AssetRequest r = getRequestForUpdate(id);
        requireNewWorkflow(r);
        if (r.getRequestType() != AssetRequestType.BRANCH_TRANSFER && r.getRequestType() != AssetRequestType.HEAD_OFFICE_TRANSFER) {
            throw new IllegalStateException("APPROVE is only valid for transfer requests");
        }
        requireStatus(r, AssetRequestStatus.RECEIVED);
        validateTransferRequest(r);
        r.setApprovedBy(by);
        r.setApprovedDate(LocalDateTime.now());
        r.setStatus(AssetRequestStatus.APPROVED);
        return repo.save(r);
    }

    @Override
    @Transactional
    public AssetRequest payBill(Long id, String by) {
        AssetRequest r = getRequestForUpdate(id);
        requireNewWorkflow(r);
        if (r.getRequestType() != AssetRequestType.PURCHASE) {
            throw new IllegalStateException("PAY_BILL is only valid for PURCHASE requests");
        }
        requireStatus(r, AssetRequestStatus.PAY_BILL);
        normalizePurchaseAmounts(r);

        if (r.getAssets() == null || r.getAssets().size() != r.getQuantity()) {
            throw new IllegalStateException("Purchase request must have exactly " + r.getQuantity() + " created Asset Master record(s) before payment");
        }
        if (r.getPurchaseCost() == null || r.getPurchaseCost().signum() <= 0) {
            throw new IllegalArgumentException("purchaseCost must be greater than zero");
        }

        // One bank debit for the COMPLETE invoice, not one debit per asset.
        bankingTransactionService.processAssetPurchase(r);
        r.setPaidBy(by);
        r.setPaidDate(LocalDateTime.now());
        r.setStatus(AssetRequestStatus.COMPLETED);
        addRemark(r, "Purchase bill paid by: " + by + "; total amount: " + r.getInvoiceAmount());
        return repo.save(r);
    }

    @Override
    @Transactional
    public AssetRequest transfer(Long id, String by) {
        AssetRequest r = getRequestForUpdate(id);
        requireNewWorkflow(r);
        if (r.getRequestType() != AssetRequestType.BRANCH_TRANSFER && r.getRequestType() != AssetRequestType.HEAD_OFFICE_TRANSFER) {
            throw new IllegalStateException("TRANSFER is only valid for branch/head-office transfer requests");
        }
        requireStatus(r, AssetRequestStatus.APPROVED);
        validateTransferRequest(r);

        Asset asset = assetRepository.findActiveById(r.getAsset().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Active asset not found: " + r.getAsset().getId()));
        if (asset.getStatus() != AssetStatus.AVAILABLE) {
            throw new IllegalStateException("Only AVAILABLE assets can be transferred through an asset request");
        }
        if (r.getSourceBranch() != null && (asset.getBranch() == null || !asset.getBranch().getId().equals(r.getSourceBranch().getId()))) {
            throw new IllegalStateException("Asset is not currently located at the request source branch");
        }

        TransferRequest transferRequest = new TransferRequest(
                r.getDestinationBranch().getId(), null, LocalDate.now(),
                "Asset Request #" + r.getId() + " transferred by " + by
        );
        Asset transferredAsset = assetTransferService.transfer(asset.getId(), transferRequest).getAsset();
        r.setAsset(transferredAsset);
        r.setReceivedAssetId(transferredAsset.getId());
        r.setStatus(AssetRequestStatus.TRANSFERRED);
        addRemark(r, "Transferred by: " + by);
        return repo.save(r);
    }

    @Override
    @Transactional
    public AssetRequest complete(Long id, String by) {
        AssetRequest r = getRequestForUpdate(id);
        requireNewWorkflow(r);
        if (r.getRequestType() == AssetRequestType.CREATE) requireStatus(r, AssetRequestStatus.RECEIVED);
        else if (r.getRequestType() == AssetRequestType.BRANCH_TRANSFER || r.getRequestType() == AssetRequestType.HEAD_OFFICE_TRANSFER) requireStatus(r, AssetRequestStatus.TRANSFERRED);
        else if (r.getRequestType() == AssetRequestType.PURCHASE) requireStatus(r, AssetRequestStatus.COMPLETED);
        else throw new IllegalStateException("Unsupported request type");

        if (r.getRequestType() != AssetRequestType.PURCHASE) r.setStatus(AssetRequestStatus.COMPLETED);
        addRemark(r, "Completed by: " + by);
        return repo.save(r);
    }

    @Override
    @Transactional
    public AssetRequest cancel(Long id, String by, String reason) {
        AssetRequest r = getRequestForUpdate(id);
        requireNewWorkflow(r);
        if (r.getStatus() == AssetRequestStatus.COMPLETED || r.getStatus() == AssetRequestStatus.TRANSFERRED) {
            throw new IllegalStateException("Completed/transferred requests cannot be cancelled");
        }
        r.setStatus(AssetRequestStatus.CANCELLED);
        addRemark(r, "Cancelled by " + by + (reason == null || reason.isBlank() ? "" : ": " + reason));
        return repo.save(r);
    }

    // Existing employee workflow retained exactly as a separate legacy path.
    @Override
    @Transactional
    public AssetRequest approveByDeptHead(Long id, String by) {
        AssetRequest r = getRequestById(id);
        if (r.getStatus() != AssetRequestStatus.SEND) throw new IllegalStateException("Request must be in SEND before department approval");
        if (currentUserService.isAuthenticated() && "Department Head".equalsIgnoreCase(currentUserService.getRoleName())) {
            UUID requesterDepartment = r.getRequestedBy() != null && r.getRequestedBy().getDepartment() != null
                    ? r.getRequestedBy().getDepartment().getId() : r.getAssetDepartmentId();
            if (requesterDepartment == null || !requesterDepartment.equals(currentUserService.getDepartmentId())) {
                throw new org.springframework.security.access.AccessDeniedException("Department Head can approve only requests from the current department");
            }
        }
        r.setDepartmentApprovedBy(by); r.setDepartmentApprovedAt(LocalDateTime.now()); r.setStatus(AssetRequestStatus.PENDING_MANAGER);
        return repo.save(r);
    }

    @Override
    @Transactional
    public AssetRequest approveByAssetManager(Long id, String by) {
        AssetRequest r = getRequestById(id);
        if (r.getStatus() != AssetRequestStatus.PENDING_MANAGER) throw new IllegalStateException("Department Head approval is required first");
        r.setManagerApprovedBy(by); r.setManagerApprovedAt(LocalDateTime.now()); r.setApprovedBy(by); r.setStatus(AssetRequestStatus.ACCEPT);
        return repo.save(r);
    }

    @Override
    @Transactional
    public AssetRequest rejectRequest(Long id, String by, String reason) {
        AssetRequest r = getRequestById(id);
        if (r.getStatus() == AssetRequestStatus.REJECT || r.getStatus() == AssetRequestStatus.REJECTED || r.getStatus() == AssetRequestStatus.FULFILLED || r.getStatus() == AssetRequestStatus.COMPLETED) {
            throw new IllegalStateException("Request cannot be rejected in its current state");
        }
        r.setStatus(isNewWorkflow(r.getRequestType()) ? AssetRequestStatus.REJECTED : AssetRequestStatus.REJECT);
        r.setApprovedBy(by);
        r.setRejectionReason(reason);
        addRemark(r, reason == null || reason.isBlank() ? "Rejected" : "Rejected: " + reason);
        return repo.save(r);
    }

    @Override
    @Transactional
    public AssetRequest fulfill(Long id, UUID assetId) {
        AssetRequest r = getRequestById(id);
        if (r.getStatus() != AssetRequestStatus.ACCEPT) throw new IllegalStateException("Only ACCEPT requests can be fulfilled");
        Employee emp = employeeRepository.findByEmployeeCode(r.getEmployeeId()).orElseThrow(() -> new ResourceNotFoundException("Employee not found for employeeId: " + r.getEmployeeId()));
        AllocateAssetRequest ar = new AllocateAssetRequest();
        ar.setAssetId(assetId); ar.setEmployeeId(emp.getId()); ar.setAllocatedOn(LocalDate.now()); ar.setAcknowledgedByEmployee(false);
        var a = allocationService.allocate(ar);
        r.setAsset(a.getAsset()); r.setFulfilledAllocationId(a.getId()); r.setStatus(AssetRequestStatus.FULFILLED);
        return repo.save(r);
    }

    private boolean isNewWorkflow(AssetRequestType type) {
        return type == AssetRequestType.CREATE || type == AssetRequestType.PURCHASE || type == AssetRequestType.BRANCH_TRANSFER || type == AssetRequestType.HEAD_OFFICE_TRANSFER;
    }

    private void requireNewWorkflow(AssetRequest r) {
        if (!isNewWorkflow(r.getRequestType())) {
            throw new IllegalStateException("This endpoint is only for CREATE, PURCHASE, BRANCH_TRANSFER or HEAD_OFFICE_TRANSFER");
        }
    }

    private void requireStatus(AssetRequest r, AssetRequestStatus expected) {
        if (r.getStatus() != expected) throw new IllegalStateException("Request must be in " + expected + " but is " + r.getStatus());
    }

    private void validateNewWorkflowRequest(AssetRequest r) {
        if (r.getRequestType() == AssetRequestType.CREATE || r.getRequestType() == AssetRequestType.PURCHASE) {
            if (r.getAsset() == null) throw new IllegalArgumentException("asset payload is required for CREATE/PURCHASE requests");
            if (r.getAsset().getCompany() == null || r.getAsset().getCompany().getId() == null) throw new IllegalArgumentException("asset.company.id is required");
            if (r.getAsset().getCategory() == null || r.getAsset().getCategory().getId() == null) throw new IllegalArgumentException("asset.category.id is required");
            if (r.getAsset().getAssetType() == null) throw new IllegalArgumentException("asset.assetType is required");
            if (r.getAsset().getName() == null || r.getAsset().getName().isBlank()) throw new IllegalArgumentException("asset.name is required");
            if (r.getAsset().getAssetCode() == null || r.getAsset().getAssetCode().isBlank()) throw new IllegalArgumentException("asset.assetCode is required");
            if (r.getRequestType() == AssetRequestType.PURCHASE) {
                if (r.getPurchaseCost() == null || r.getPurchaseCost().signum() <= 0) throw new IllegalArgumentException("purchaseCost must be greater than zero");
            }
            validateQuantity(r);
        } else {
            validateTransferRequest(r);
        }
    }

    private void validateQuantity(AssetRequest r) {
        int quantity = r.getQuantity() == null ? 1 : r.getQuantity();
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be greater than zero");
        r.setQuantity(quantity);
    }

    private void normalizePurchaseAmounts(AssetRequest r) {
        if (r.getRequestType() != AssetRequestType.PURCHASE) return;
        validateQuantity(r);
        if (r.getPurchaseCost() == null || r.getPurchaseCost().signum() <= 0) {
            throw new IllegalArgumentException("purchaseCost must be greater than zero");
        }
        r.setInvoiceAmount(r.getPurchaseCost().multiply(BigDecimal.valueOf(r.getQuantity())));
    }

    private void captureAssetTemplate(AssetRequest r) {
        Asset template = r.getAsset();
        if (template == null) return;

        r.setAssetName(template.getName());
        r.setAssetCode(template.getAssetCode());
        r.setAssetSerialNumber(template.getSerialNumber());
        r.setAssetType(template.getAssetType());
        r.setAssetCompanyId(template.getCompany() == null ? null : template.getCompany().getId());
        r.setAssetBranchId(template.getBranch() == null ? null : template.getBranch().getId());
        r.setAssetDepartmentId(template.getDepartment() == null ? null : template.getDepartment().getId());
        r.setAssetCategoryId(template.getCategory() == null ? null : template.getCategory().getId());
        r.setAssetModelId(template.getModel() == null ? null : template.getModel().getId());
        r.setAssetCurrentValue(template.getCurrentValue());
        r.setAssetPurchaseDate(template.getPurchaseDate());
        r.setAssetWarrantyExpiry(template.getWarrantyExpiry());
        r.setAssetStatus(template.getStatus());
        r.setAssetCurrentLocation(template.getCurrentLocation());
    }

    private void copyAssetTemplate(AssetRequest source, AssetRequest target) {
        if (source.getAsset() != null) {
            captureAssetTemplate(source);
        }
        target.setAssetName(source.getAssetName());
        target.setAssetCode(source.getAssetCode());
        target.setAssetSerialNumber(source.getAssetSerialNumber());
        target.setAssetType(source.getAssetType());
        target.setAssetCompanyId(source.getAssetCompanyId());
        target.setAssetBranchId(source.getAssetBranchId());
        target.setAssetDepartmentId(source.getAssetDepartmentId());
        target.setAssetCategoryId(source.getAssetCategoryId());
        target.setAssetModelId(source.getAssetModelId());
        target.setAssetCurrentValue(source.getAssetCurrentValue());
        target.setAssetPurchaseDate(source.getAssetPurchaseDate());
        target.setAssetWarrantyExpiry(source.getAssetWarrantyExpiry());
        target.setAssetStatus(source.getAssetStatus());
        target.setAssetCurrentLocation(source.getAssetCurrentLocation());
    }

    private List<Asset> createAssetsFromRequest(AssetRequest r) {
        if (r.getAssetName() == null || r.getAssetName().isBlank()) throw new IllegalStateException("Asset template name is missing");
        if (r.getAssetCode() == null || r.getAssetCode().isBlank()) throw new IllegalStateException("Asset template code is missing");
        if (r.getAssetCategoryId() == null) throw new IllegalStateException("Asset template category is missing");
        if (r.getAssetType() == null) throw new IllegalStateException("Asset template assetType is missing");
        if (r.getAssetCompanyId() == null && r.getDestinationCompany() == null) throw new IllegalStateException("Asset company is missing");

        int quantity = r.getQuantity() == null ? 1 : r.getQuantity();
        if (quantity <= 0) throw new IllegalStateException("quantity must be greater than zero");

        UUID companyId = r.getAssetCompanyId() != null ? r.getAssetCompanyId() : r.getDestinationCompany().getId();
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset company not found: " + companyId));

        Branch branch = null;
        UUID branchId = r.getAssetBranchId() != null ? r.getAssetBranchId() : (r.getDestinationBranch() == null ? null : r.getDestinationBranch().getId());
        if (branchId != null) {
            branch = branchRepository.findById(branchId)
                    .orElseThrow(() -> new ResourceNotFoundException("Asset branch not found: " + branchId));
        }

        List<Asset> created = new ArrayList<>();
        for (int i = 1; i <= quantity; i++) {
            Asset copy = new Asset();
            copy.setCompany(company);
            copy.setBranch(branch);

            if (r.getAssetDepartmentId() != null) {
                Department department = new Department();
                department.setId(r.getAssetDepartmentId());
                copy.setDepartment(department);
            }

            AssetCategory category = new AssetCategory();
            category.setId(r.getAssetCategoryId());
            copy.setCategory(category);

            if (r.getAssetModelId() != null) {
                AssetModel model = new AssetModel();
                model.setId(r.getAssetModelId());
                copy.setModel(model);
            }

            copy.setAssetType(r.getAssetType());
            copy.setName(r.getAssetName());
            copy.setAssetCode(buildUniqueValue(r.getAssetCode(), i, quantity, 30));
            copy.setSerialNumber(buildUniqueValue(r.getAssetSerialNumber(), i, quantity, 100));
            copy.setVendor(r.getVendor());
            copy.setPurchaseDate(r.getAssetPurchaseDate() == null ? LocalDate.now() : r.getAssetPurchaseDate());
            copy.setPurchaseCost(r.getRequestType() == AssetRequestType.PURCHASE ? r.getPurchaseCost() : null);
            copy.setCurrentValue(r.getAssetCurrentValue());
            copy.setWarrantyExpiry(r.getAssetWarrantyExpiry());
            copy.setStatus(r.getAssetStatus() == null ? AssetStatus.AVAILABLE : r.getAssetStatus());
            copy.setCurrentLocation(r.getAssetCurrentLocation());

            // AssetService generates unique QR/barcode values after persistence.
            copy.setQrCodeValue(null);
            copy.setBarcodeValue(null);

            created.add(assetService.save(copy));
        }
        return created;
    }

    private String buildUniqueValue(String base, int index, int quantity, int maxLength) {
        if (base == null || base.isBlank()) return null;
        if (quantity <= 1) return base.length() <= maxLength ? base : base.substring(0, maxLength);
        String suffix = "-" + index;
        int available = maxLength - suffix.length();
        if (available <= 0) return suffix.substring(Math.max(0, suffix.length() - maxLength));
        String prefix = base.length() > available ? base.substring(0, available) : base;
        return prefix + suffix;
    }

    private void setCreatedAssets(AssetRequest r, List<Asset> createdAssets) {
        if (createdAssets == null || createdAssets.isEmpty()) throw new IllegalStateException("No Asset Master records were created");
        r.setAssets(createdAssets);
        r.setAsset(createdAssets.get(0));
        r.setReceivedAssetId(createdAssets.get(0).getId());
    }

    private void validateTransferRequest(AssetRequest r) {
        if (r.getAsset() == null || r.getAsset().getId() == null) throw new IllegalArgumentException("asset.id is required for transfer requests");
        if (r.getSourceBranch() == null || r.getSourceBranch().getId() == null) throw new IllegalArgumentException("sourceBranch.id is required");
        if (r.getDestinationBranch() == null || r.getDestinationBranch().getId() == null) throw new IllegalArgumentException("destinationBranch.id is required");
        if (r.getSourceBranch().getId().equals(r.getDestinationBranch().getId())) throw new IllegalArgumentException("Source and destination branches must be different");
        if (r.getSourceCompany() != null && r.getDestinationCompany() != null && !r.getSourceCompany().getId().equals(r.getDestinationCompany().getId())) throw new IllegalArgumentException("Source and destination companies must match for an internal transfer");
    }

    private void resolveReferences(AssetRequest r) {
        if (r.getSourceCompany() != null && r.getSourceCompany().getId() != null) r.setSourceCompany(companyRepository.findById(r.getSourceCompany().getId()).orElseThrow(() -> new ResourceNotFoundException("Source company not found")));
        if (r.getDestinationCompany() != null && r.getDestinationCompany().getId() != null) r.setDestinationCompany(companyRepository.findById(r.getDestinationCompany().getId()).orElseThrow(() -> new ResourceNotFoundException("Destination company not found")));
        if (r.getSourceBranch() != null && r.getSourceBranch().getId() != null) r.setSourceBranch(branchRepository.findById(r.getSourceBranch().getId()).orElseThrow(() -> new ResourceNotFoundException("Source branch not found")));
        if (r.getDestinationBranch() != null && r.getDestinationBranch().getId() != null) r.setDestinationBranch(branchRepository.findById(r.getDestinationBranch().getId()).orElseThrow(() -> new ResourceNotFoundException("Destination branch not found")));
        if (r.getRequestedBy() != null && r.getRequestedBy().getId() != null) r.setRequestedBy(employeeRepository.findById(r.getRequestedBy().getId()).orElseThrow(() -> new ResourceNotFoundException("requestedBy employee not found")));
    }

    private Asset resolveExistingAsset(Asset incoming) {
        if (incoming == null || incoming.getId() == null) return null;
        return assetRepository.findById(incoming.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found: " + incoming.getId()));
    }

    private void addRemark(AssetRequest r, String text) {
        r.setRemarks((r.getRemarks() == null || r.getRemarks().isBlank()) ? text : r.getRemarks() + "\n" + text);
    }
}

/*
package com.example.assets_management.asset_requests.service;

import com.example.assets_management.asset_allocation.dto.request.AllocateAssetRequest;
import com.example.assets_management.asset_allocation.service.AllocationService;
import com.example.assets_management.asset_history.dto.request.AssetHistoryRequest;
import com.example.assets_management.asset_history.service.AssetHistoryService;
import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.asset_master.enums.AssetStatus;
import com.example.assets_management.asset_master.repository.AssetRepository;
import com.example.assets_management.asset_master.service.AssetService;
import com.example.assets_management.asset_requests.entity.AssetRequest;
import com.example.assets_management.asset_requests.enums.AssetRequestStatus;
import com.example.assets_management.asset_requests.enums.AssetRequestType;
import com.example.assets_management.asset_requests.repository.AssetRequestRepository;
import com.example.assets_management.branch.entity.Branch;
import com.example.assets_management.branch.repository.BranchRepository;
import com.example.assets_management.company.repository.CompanyRepository;
import com.example.assets_management.employee.entity.Employee;
import com.example.assets_management.employee.repository.EmployeeRepository;
import com.example.assets_management.exception.ResourceNotFoundException;
import com.example.assets_management.asset_transfer.dto.TransferRequest;
import com.example.assets_management.asset_transfer.service.AssetTransferService;
import com.example.assets_management.banking.transaction.service.BankingTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssetRequestServiceImpl implements AssetRequestService {

    private final AssetRequestRepository repo;
    private final AllocationService allocationService;
    private final EmployeeRepository employeeRepository;
    private final AssetRepository assetRepository;
    private final AssetService assetService;
    private final AssetTransferService assetTransferService;
    private final BankingTransactionService bankingTransactionService;
    private final BranchRepository branchRepository;
    private final CompanyRepository companyRepository;
    private final AssetHistoryService assetHistoryService;

    @Override
    @Transactional
    public AssetRequest createRequest(AssetRequest r) {
        if (r.getRequestType() == null) throw new IllegalArgumentException("requestType is required");
        if (r.getPriority() == null) r.setPriority(com.example.assets_management.asset_requests.enums.AssetRequestPriority.MEDIUM);
        if (r.getRequestDate() == null) r.setRequestDate(LocalDate.now());

        if (isNewWorkflow(r.getRequestType())) {
            if (r.getStatus() == null) r.setStatus(AssetRequestStatus.DRAFT);
            if (r.getStatus() != AssetRequestStatus.DRAFT) throw new IllegalStateException("New workflow requests must start in DRAFT");
            validateNewWorkflowRequest(r);
            resolveReferences(r);
        } else {
            if (r.getStatus() == null) r.setStatus(AssetRequestStatus.SEND);
        }
        return repo.save(r);
    }

    @Override
    @Transactional
    public AssetRequest updateRequest(Long id, AssetRequest r) {
        AssetRequest e = getRequestById(id);
        if (isNewWorkflow(e.getRequestType()) && e.getStatus() != AssetRequestStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT workflow requests can be updated");
        }
        e.setRequestType(r.getRequestType());
        e.setAsset(r.getAsset());
        e.setRequestedCategory(r.getRequestedCategory());
        e.setEmployeeId(r.getEmployeeId());
        e.setEmployeeName(r.getEmployeeName());
        e.setDepartment(r.getDepartment());
        e.setRequestedBy(r.getRequestedBy());
        e.setRequestDate(r.getRequestDate());
        e.setPriority(r.getPriority() == null ? e.getPriority() : r.getPriority());
        e.setSourceCompany(r.getSourceCompany());
        e.setSourceBranch(r.getSourceBranch());
        e.setDestinationCompany(r.getDestinationCompany());
        e.setDestinationBranch(r.getDestinationBranch());
        e.setVendor(r.getVendor());
        e.setPurchaseCost(r.getPurchaseCost());
        e.setInvoiceNumber(r.getInvoiceNumber());
        e.setInvoiceAmount(r.getInvoiceAmount());
        e.setQuantity(r.getQuantity());
        e.setRemarks(r.getRemarks());
        resolveReferences(e);
        return repo.save(e);
    }

    @Override
    public void deleteRequest(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("AssetRequest not found with id: " + id);
        repo.deleteById(id);
    }

    @Override
    public AssetRequest getRequestById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("AssetRequest not found with id: " + id));
    }

    private AssetRequest getRequestForUpdate(Long id) {
        return repo.findByIdForUpdate(id).orElseThrow(() -> new ResourceNotFoundException("AssetRequest not found with id: " + id));
    }

    @Override public List<AssetRequest> getAllRequests() { return repo.findAll(); }
    @Override public List<AssetRequest> getRequestsByAssetId(UUID id) { return repo.findByAsset_Id(id); }
    @Override public List<AssetRequest> getRequestsByStatus(String status) { return repo.findByStatus(AssetRequestStatus.valueOf(status.toUpperCase())); }

    @Override
    @Transactional
    public AssetRequest send(Long id, String by) {
        AssetRequest r = getRequestForUpdate(id);
        requireNewWorkflow(r);
        requireStatus(r, AssetRequestStatus.DRAFT);
        r.setStatus(AssetRequestStatus.SENT);
        addRemark(r, "Sent by: " + by);
        return repo.save(r);
    }

    @Override
    @Transactional
    public AssetRequest receive(Long id, String by) {
        AssetRequest r = getRequestForUpdate(id);
        requireNewWorkflow(r);
        requireStatus(r, AssetRequestStatus.SENT);
        r.setReceivedBy(by);
        r.setReceivedDate(LocalDateTime.now());

        if (r.getRequestType() == AssetRequestType.CREATE) {
            Asset asset = createAssetFromRequest(r);
            r.setAsset(asset);
            r.setReceivedAssetId(asset.getId());
            r.setStatus(AssetRequestStatus.RECEIVED);
        } else if (r.getRequestType() == AssetRequestType.PURCHASE) {
            // Receiving the physical asset is a separate business event from creating
            // the Asset Master record and from paying the invoice.
            r.setStatus(AssetRequestStatus.RECEIVED_ASSET);
        } else {
            validateTransferRequest(r);
            r.setStatus(AssetRequestStatus.RECEIVED);
        }
        return repo.save(r);
    }

    @Override
    @Transactional
    public AssetRequest createAsset(Long id, String by) {
        AssetRequest r = getRequestForUpdate(id);
        requireNewWorkflow(r);
        if (r.getRequestType() != AssetRequestType.PURCHASE) throw new IllegalStateException("CREATE_ASSET is only valid for PURCHASE requests");
        requireStatus(r, AssetRequestStatus.RECEIVED_ASSET);
        Asset asset = createAssetFromRequest(r);
        r.setAsset(asset);
        r.setReceivedAssetId(asset.getId());
        r.setStatus(AssetRequestStatus.PAY_BILL);
        addRemark(r, "Asset Master created by: " + by);
        return repo.save(r);
    }

    @Override
    @Transactional
    public AssetRequest approve(Long id, String by) {
        AssetRequest r = getRequestForUpdate(id);
        requireNewWorkflow(r);
        if (r.getRequestType() != AssetRequestType.BRANCH_TRANSFER && r.getRequestType() != AssetRequestType.HEAD_OFFICE_TRANSFER) {
            throw new IllegalStateException("APPROVE is only valid for transfer requests");
        }
        requireStatus(r, AssetRequestStatus.RECEIVED);
        validateTransferRequest(r);
        r.setApprovedBy(by);
        r.setApprovedDate(LocalDateTime.now());
        r.setStatus(AssetRequestStatus.APPROVED);
        return repo.save(r);
    }

    @Override
    @Transactional
    public AssetRequest payBill(Long id, String by) {
        AssetRequest r = getRequestForUpdate(id);
        requireNewWorkflow(r);
        if (r.getRequestType() != AssetRequestType.PURCHASE) throw new IllegalStateException("PAY_BILL is only valid for PURCHASE requests");
        requireStatus(r, AssetRequestStatus.PAY_BILL);
        if (r.getAsset() == null) throw new IllegalStateException("Purchase request has no received asset");
        if (r.getPurchaseCost() == null || r.getPurchaseCost().signum() <= 0) throw new IllegalArgumentException("purchaseCost must be greater than zero");

        // Banking service performs a locked balance check and creates the ASSET_PURCHASE debit atomically.
        bankingTransactionService.processAssetPurchase(r.getAsset());
        r.setPaidBy(by);
        r.setPaidDate(LocalDateTime.now());
        r.setStatus(AssetRequestStatus.COMPLETED);
        return repo.save(r);
    }

    @Override
    @Transactional
    public AssetRequest transfer(Long id, String by) {
        AssetRequest r = getRequestForUpdate(id);
        requireNewWorkflow(r);
        if (r.getRequestType() != AssetRequestType.BRANCH_TRANSFER && r.getRequestType() != AssetRequestType.HEAD_OFFICE_TRANSFER) {
            throw new IllegalStateException("TRANSFER is only valid for branch/head-office transfer requests");
        }
        requireStatus(r, AssetRequestStatus.APPROVED);
        validateTransferRequest(r);

        Asset asset = assetRepository.findActiveById(r.getAsset().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Active asset not found: " + r.getAsset().getId()));
        if (asset.getStatus() != AssetStatus.AVAILABLE) {
            throw new IllegalStateException("Only AVAILABLE assets can be transferred through an asset request");
        }
        if (r.getSourceBranch() != null && (asset.getBranch() == null || !asset.getBranch().getId().equals(r.getSourceBranch().getId()))) {
            throw new IllegalStateException("Asset is not currently located at the request source branch");
        }

        TransferRequest transferRequest = new TransferRequest(
                r.getDestinationBranch().getId(), null, LocalDate.now(),
                "Asset Request #" + r.getId() + " transferred by " + by
        );
        Asset transferredAsset = assetTransferService.transfer(asset.getId(), transferRequest).getAsset();
        r.setAsset(transferredAsset);
        r.setReceivedAssetId(transferredAsset.getId());
        r.setStatus(AssetRequestStatus.TRANSFERRED);
        addRemark(r, "Transferred by: " + by);
        return repo.save(r);
    }

    @Override
    @Transactional
    public AssetRequest complete(Long id, String by) {
        AssetRequest r = getRequestForUpdate(id);
        requireNewWorkflow(r);
        if (r.getRequestType() == AssetRequestType.CREATE) requireStatus(r, AssetRequestStatus.RECEIVED);
        else if (r.getRequestType() == AssetRequestType.BRANCH_TRANSFER || r.getRequestType() == AssetRequestType.HEAD_OFFICE_TRANSFER) requireStatus(r, AssetRequestStatus.TRANSFERRED);
        else if (r.getRequestType() == AssetRequestType.PURCHASE) requireStatus(r, AssetRequestStatus.COMPLETED);
        else throw new IllegalStateException("Unsupported request type");

        if (r.getRequestType() != AssetRequestType.PURCHASE) r.setStatus(AssetRequestStatus.COMPLETED);
        addRemark(r, "Completed by: " + by);
        return repo.save(r);
    }

    @Override
    @Transactional
    public AssetRequest cancel(Long id, String by, String reason) {
        AssetRequest r = getRequestForUpdate(id);
        requireNewWorkflow(r);
        if (r.getStatus() == AssetRequestStatus.COMPLETED || r.getStatus() == AssetRequestStatus.TRANSFERRED) throw new IllegalStateException("Completed/transferred requests cannot be cancelled");
        r.setStatus(AssetRequestStatus.CANCELLED);
        addRemark(r, "Cancelled by " + by + (reason == null || reason.isBlank() ? "" : ": " + reason));
        return repo.save(r);
    }

    // Existing employee workflow retained exactly as a separate legacy path.
    @Override
    @Transactional
    public AssetRequest approveByDeptHead(Long id, String by) {
        AssetRequest r = getRequestById(id);
        if (r.getStatus() != AssetRequestStatus.SEND) throw new IllegalStateException("Request must be in SEND before department approval");
        r.setDepartmentApprovedBy(by); r.setDepartmentApprovedAt(LocalDateTime.now()); r.setStatus(AssetRequestStatus.PENDING_MANAGER);
        return repo.save(r);
    }

    @Override
    @Transactional
    public AssetRequest approveByAssetManager(Long id, String by) {
        AssetRequest r = getRequestById(id);
        if (r.getStatus() != AssetRequestStatus.PENDING_MANAGER) throw new IllegalStateException("Department Head approval is required first");
        r.setManagerApprovedBy(by); r.setManagerApprovedAt(LocalDateTime.now()); r.setApprovedBy(by); r.setStatus(AssetRequestStatus.ACCEPT);
        return repo.save(r);
    }

    @Override
    @Transactional
    public AssetRequest rejectRequest(Long id, String by, String reason) {
        AssetRequest r = getRequestById(id);
        if (r.getStatus() == AssetRequestStatus.REJECT || r.getStatus() == AssetRequestStatus.REJECTED || r.getStatus() == AssetRequestStatus.FULFILLED || r.getStatus() == AssetRequestStatus.COMPLETED) throw new IllegalStateException("Request cannot be rejected in its current state");
        r.setStatus(isNewWorkflow(r.getRequestType()) ? AssetRequestStatus.REJECTED : AssetRequestStatus.REJECT);
        r.setApprovedBy(by);
        r.setRejectionReason(reason);
        addRemark(r, reason == null || reason.isBlank() ? "Rejected" : "Rejected: " + reason);
        return repo.save(r);
    }

    @Override
    @Transactional
    public AssetRequest fulfill(Long id, UUID assetId) {
        AssetRequest r = getRequestById(id);
        if (r.getStatus() != AssetRequestStatus.ACCEPT) throw new IllegalStateException("Only ACCEPT requests can be fulfilled");
        Employee emp = employeeRepository.findByEmployeeCode(r.getEmployeeId()).orElseThrow(() -> new ResourceNotFoundException("Employee not found for employeeId: " + r.getEmployeeId()));
        AllocateAssetRequest ar = new AllocateAssetRequest();
        ar.setAssetId(assetId); ar.setEmployeeId(emp.getId()); ar.setAllocatedOn(LocalDate.now()); ar.setAcknowledgedByEmployee(false);
        var a = allocationService.allocate(ar);
        r.setAsset(a.getAsset()); r.setFulfilledAllocationId(a.getId()); r.setStatus(AssetRequestStatus.FULFILLED);
        return repo.save(r);
    }

    private boolean isNewWorkflow(AssetRequestType type) {
        return type == AssetRequestType.CREATE || type == AssetRequestType.PURCHASE || type == AssetRequestType.BRANCH_TRANSFER || type == AssetRequestType.HEAD_OFFICE_TRANSFER;
    }

    private void requireNewWorkflow(AssetRequest r) { if (!isNewWorkflow(r.getRequestType())) throw new IllegalStateException("This endpoint is only for CREATE, PURCHASE, BRANCH_TRANSFER or HEAD_OFFICE_TRANSFER"); }
    private void requireStatus(AssetRequest r, AssetRequestStatus expected) { if (r.getStatus() != expected) throw new IllegalStateException("Request must be in " + expected + " but is " + r.getStatus()); }

    private void validateNewWorkflowRequest(AssetRequest r) {
        if (r.getRequestType() == AssetRequestType.CREATE || r.getRequestType() == AssetRequestType.PURCHASE) {
            if (r.getAsset() == null) throw new IllegalArgumentException("asset payload is required for CREATE/PURCHASE requests");
            if (r.getAsset().getCompany() == null || r.getAsset().getCompany().getId() == null) throw new IllegalArgumentException("asset.company.id is required");
            if (r.getAsset().getCategory() == null || r.getAsset().getCategory().getId() == null) throw new IllegalArgumentException("asset.category.id is required");
            if (r.getRequestType() == AssetRequestType.PURCHASE) {
                if (r.getPurchaseCost() == null || r.getPurchaseCost().signum() <= 0) throw new IllegalArgumentException("purchaseCost must be greater than zero");
            }
        } else {
            validateTransferRequest(r);
        }
    }

    private void validateTransferRequest(AssetRequest r) {
        if (r.getAsset() == null || r.getAsset().getId() == null) throw new IllegalArgumentException("asset.id is required for transfer requests");
        if (r.getSourceBranch() == null || r.getSourceBranch().getId() == null) throw new IllegalArgumentException("sourceBranch.id is required");
        if (r.getDestinationBranch() == null || r.getDestinationBranch().getId() == null) throw new IllegalArgumentException("destinationBranch.id is required");
        if (r.getSourceBranch().getId().equals(r.getDestinationBranch().getId())) throw new IllegalArgumentException("Source and destination branches must be different");
        if (r.getSourceCompany() != null && r.getDestinationCompany() != null && !r.getSourceCompany().getId().equals(r.getDestinationCompany().getId())) throw new IllegalArgumentException("Source and destination companies must match for an internal transfer");
    }

    private void resolveReferences(AssetRequest r) {
        if (r.getSourceCompany() != null && r.getSourceCompany().getId() != null) r.setSourceCompany(companyRepository.findById(r.getSourceCompany().getId()).orElseThrow(() -> new ResourceNotFoundException("Source company not found")));
        if (r.getDestinationCompany() != null && r.getDestinationCompany().getId() != null) r.setDestinationCompany(companyRepository.findById(r.getDestinationCompany().getId()).orElseThrow(() -> new ResourceNotFoundException("Destination company not found")));
        if (r.getSourceBranch() != null && r.getSourceBranch().getId() != null) r.setSourceBranch(branchRepository.findById(r.getSourceBranch().getId()).orElseThrow(() -> new ResourceNotFoundException("Source branch not found")));
        if (r.getDestinationBranch() != null && r.getDestinationBranch().getId() != null) r.setDestinationBranch(branchRepository.findById(r.getDestinationBranch().getId()).orElseThrow(() -> new ResourceNotFoundException("Destination branch not found")));
        if (r.getRequestedBy() != null && r.getRequestedBy().getId() != null) r.setRequestedBy(employeeRepository.findById(r.getRequestedBy().getId()).orElseThrow(() -> new ResourceNotFoundException("requestedBy employee not found")));
        if (r.getVendor() != null && r.getVendor().getId() != null) r.setVendor(r.getVendor());
    }

    private Asset createAssetFromRequest(AssetRequest r) {
        Asset template = r.getAsset();
        if (template == null) throw new IllegalArgumentException("asset payload is required to create the asset");
        Asset copy = new Asset();
        copy.setCompany(template.getCompany());
        copy.setBranch(r.getDestinationBranch() != null ? r.getDestinationBranch() : template.getBranch());
        copy.setDepartment(template.getDepartment());
        copy.setAssetCode(template.getAssetCode());
        copy.setName(template.getName());
        copy.setCategory(template.getCategory());
        copy.setModel(template.getModel());
        copy.setAssetType(template.getAssetType());
        copy.setSerialNumber(template.getSerialNumber());
        copy.setVendor(r.getVendor() != null ? r.getVendor() : template.getVendor());
        copy.setPurchaseDate(template.getPurchaseDate() == null ? LocalDate.now() : template.getPurchaseDate());
        copy.setPurchaseCost(r.getRequestType() == AssetRequestType.PURCHASE ? r.getPurchaseCost() : template.getPurchaseCost());
        copy.setCurrentValue(template.getCurrentValue());
        copy.setWarrantyExpiry(template.getWarrantyExpiry());
        copy.setStatus(template.getStatus() == null ? AssetStatus.AVAILABLE : template.getStatus());
        copy.setCurrentOwner(template.getCurrentOwner());
        copy.setCurrentLocation(template.getCurrentLocation());
        copy.setQrCodeValue(template.getQrCodeValue());
        copy.setBarcodeValue(template.getBarcodeValue());
        Asset saved = assetService.save(copy);
        return saved;
    }

    private void addRemark(AssetRequest r, String text) { r.setRemarks((r.getRemarks() == null || r.getRemarks().isBlank()) ? text : r.getRemarks() + "\n" + text); }
}
*/