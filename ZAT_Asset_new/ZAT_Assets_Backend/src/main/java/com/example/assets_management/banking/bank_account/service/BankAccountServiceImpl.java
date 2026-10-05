package com.example.assets_management.banking.bank_account.service;

import com.example.assets_management.banking.bank_account.dto.request.BankAccountRequest;
import com.example.assets_management.banking.bank_account.dto.request.BankAccountStatusRequest;
import com.example.assets_management.banking.bank_account.dto.response.BankAccountResponse;
import com.example.assets_management.banking.bank_account.entity.BankAccount;
import com.example.assets_management.banking.bank_account.enums.BankAccountHolderType;
import com.example.assets_management.banking.bank_account.enums.BankAccountStatus;
import com.example.assets_management.banking.transaction.dto.response.TransactionResponse;
import com.example.assets_management.banking.transaction.entity.BankTransaction;
import com.example.assets_management.banking.transaction.repository.BankTransactionRepository;
import com.example.assets_management.banking.transfer.dto.response.TransferResponse;
import com.example.assets_management.banking.transfer.entity.BankTransfer;
import com.example.assets_management.banking.transfer.repository.BankTransferRepository;
import com.example.assets_management.branch.entity.Branch;
import com.example.assets_management.company.entity.Company;
import com.example.assets_management.company.repository.CompanyRepository;
import com.example.assets_management.employee.entity.Employee;
import com.example.assets_management.employee.repository.EmployeeRepository;
import com.example.assets_management.exception.ResourceNotFoundException;
import com.example.assets_management.vendor.entity.Vendor;
import com.example.assets_management.vendor.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {
    private final com.example.assets_management.banking.bank_account.repository.BankAccountRepository repository;
    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;
    private final VendorRepository vendorRepository;
    private final BankTransactionRepository transactionRepository;
    private final BankTransferRepository transferRepository;

    @Override @Transactional
    public BankAccountResponse create(BankAccountRequest request) {
        validateRequest(request);
        String accountNumber = request.getAccountNumber().trim();
        if (repository.existsByCompany_IdAndAccountNumber(request.getCompanyId(), accountNumber)) {
            throw new IllegalArgumentException("An account with this accountNumber already exists for the company");
        }
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + request.getCompanyId()));
        BankAccount account = new BankAccount();
        apply(account, request, company);
        account.setAccountNumber(accountNumber);
        account.setOpeningBalance(request.getOpeningBalance());
        account.setCurrentBalance(request.getOpeningBalance());
        return toResponse(repository.save(account));
    }

    @Override @Transactional(readOnly=true)
    public List<BankAccountResponse> findAll(UUID companyId) {
        List<BankAccount> list = companyId == null ? repository.findAll() : repository.findByCompany_IdOrderByAccountNameAsc(companyId);
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override @Transactional(readOnly=true)
    public BankAccountResponse findById(UUID id) { return toResponse(get(id)); }

    @Override @Transactional
    public BankAccountResponse update(UUID id, BankAccountRequest request) {
        validateRequest(request);
        BankAccount existing = get(id);
        if (existing.getStatus() == BankAccountStatus.CLOSED) throw new IllegalStateException("Closed bank account cannot be updated");
        if (!existing.getCompany().getId().equals(request.getCompanyId())) throw new IllegalArgumentException("companyId cannot be changed");
        String accountNumber = request.getAccountNumber().trim();
        if (!existing.getAccountNumber().equals(accountNumber) && repository.existsByCompany_IdAndAccountNumber(request.getCompanyId(), accountNumber)) {
            throw new IllegalArgumentException("An account with this accountNumber already exists for the company");
        }
        apply(existing, request, existing.getCompany());
        existing.setAccountNumber(accountNumber);
        if (request.getOpeningBalance() != null && existing.getOpeningBalance() != null && request.getOpeningBalance().compareTo(existing.getOpeningBalance()) != 0) {
            throw new IllegalArgumentException("openingBalance cannot be changed after account creation");
        }
        return toResponse(repository.save(existing));
    }

    @Override @Transactional
    public BankAccountResponse changeStatus(UUID id, BankAccountStatusRequest request) {
        BankAccount account = get(id);
        BankAccountStatus next = request.getStatus();
        if (account.getStatus() == BankAccountStatus.CLOSED && next != BankAccountStatus.CLOSED) throw new IllegalStateException("Closed account cannot be reopened");
        if (next == BankAccountStatus.CLOSED) account.setClosedAt(java.time.LocalDate.now());
        if (next != BankAccountStatus.CLOSED) account.setClosedAt(null);
        account.setStatus(next);
        return toResponse(repository.save(account));
    }

    @Override @Transactional(readOnly=true)
    public BigDecimal getBalance(UUID id) { return get(id).getCurrentBalance(); }

    @Override @Transactional(readOnly=true)
    public List<TransactionResponse> getTransactions(UUID id) {
        get(id);
        return transactionRepository.findByBankAccount_IdAndIsDeletedFalseOrderByTransactionDateDescCreatedAtDesc(id)
                .stream().map(this::transactionResponse).collect(Collectors.toList());
    }

    @Override @Transactional(readOnly=true)
    public List<TransferResponse> getTransfers(UUID id) {
        get(id);
        return transferRepository.findByFromAccount_IdOrToAccount_IdOrderByTransferDateDescCreatedAtDesc(id, id)
                .stream().map(this::transferResponse).collect(Collectors.toList());
    }

    private void validateRequest(BankAccountRequest request) {
        if (request.getAccountNumber() == null || !request.getAccountNumber().matches("^[0-9]{9,18}$")) throw new IllegalArgumentException("accountNumber must contain 9 to 18 digits only");
        if (request.getIfscCode() != null && !request.getIfscCode().matches("^[A-Z]{4}0[A-Z0-9]{6}$")) throw new IllegalArgumentException("ifscCode must be a valid 11-character IFSC code");
        if (request.getOpeningBalance() == null || request.getOpeningBalance().signum() < 0) throw new IllegalArgumentException("openingBalance cannot be negative");
        if (request.getAccountHolderType() == null) throw new IllegalArgumentException("accountHolderType is required");
        if (request.getAccountHolderType() == BankAccountHolderType.COMPANY && (request.getEmployeeId()!=null || request.getVendorId()!=null)) throw new IllegalArgumentException("COMPANY account cannot have employeeId or vendorId");
        if (request.getAccountHolderType() == BankAccountHolderType.EMPLOYEE && (request.getEmployeeId()==null || request.getVendorId()!=null)) throw new IllegalArgumentException("EMPLOYEE account requires employeeId and cannot have vendorId");
        if (request.getAccountHolderType() == BankAccountHolderType.VENDOR && (request.getVendorId()==null || request.getEmployeeId()!=null)) throw new IllegalArgumentException("VENDOR account requires vendorId and cannot have employeeId");
    }

    private void apply(BankAccount target, BankAccountRequest request, Company company) {
        target.setCompany(company);
        target.setAccountName(request.getAccountName().trim());
        target.setAccountType(request.getAccountType());
        target.setAccountHolderType(request.getAccountHolderType());
        target.setBankName(request.getBankName());
        target.setBranchName(request.getBranchName());
        target.setIfscCode(request.getIfscCode() == null ? null : request.getIfscCode().trim().toUpperCase());
        target.setCurrency(request.getCurrency() == null || request.getCurrency().isBlank() ? "INR" : request.getCurrency().trim().toUpperCase());
        target.setIsPrimary(Boolean.TRUE.equals(request.getIsPrimary()));
        target.setExternalAccountId(request.getExternalAccountId());
        target.setSource(request.getSource() == null || request.getSource().isBlank() ? "INTERNAL" : request.getSource().trim().toUpperCase());
        target.setOpenedAt(request.getOpenedAt() == null ? java.time.LocalDate.now() : request.getOpenedAt());
        if (request.getAccountHolderType() == BankAccountHolderType.EMPLOYEE) {
            Employee employee = employeeRepository.findById(request.getEmployeeId()).orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + request.getEmployeeId()));
            if (!employee.getCompany().getId().equals(company.getId())) throw new IllegalArgumentException("Employee must belong to the selected company");
            target.setEmployee(employee); target.setVendor(null);
        } else if (request.getAccountHolderType() == BankAccountHolderType.VENDOR) {
            Vendor vendor = vendorRepository.findById(request.getVendorId()).orElseThrow(() -> new ResourceNotFoundException("Vendor not found: " + request.getVendorId()));
            if (!vendor.getCompany().getId().equals(company.getId())) throw new IllegalArgumentException("Vendor must belong to the selected company");
            target.setVendor(vendor); target.setEmployee(null);
        } else { target.setEmployee(null); target.setVendor(null); }
    }
    private BankAccount get(UUID id) { return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Bank account not found: " + id)); }
    private BankAccountResponse toResponse(BankAccount a) {
        String n=a.getAccountNumber(); String masked=n == null ? null : "*".repeat(Math.max(0,n.length()-4))+n.substring(Math.max(0,n.length()-4));
        return BankAccountResponse.builder().id(a.getId()).companyId(a.getCompany().getId()).accountNumber(masked).accountName(a.getAccountName()).accountType(a.getAccountType()).accountHolderType(a.getAccountHolderType()).employeeId(a.getEmployee()==null?null:a.getEmployee().getId()).vendorId(a.getVendor()==null?null:a.getVendor().getId()).bankName(a.getBankName()).branchName(a.getBranchName()).ifscCode(a.getIfscCode()).currency(a.getCurrency()).openingBalance(a.getOpeningBalance()).currentBalance(a.getCurrentBalance()).status(a.getStatus()).isPrimary(a.getIsPrimary()).externalAccountId(a.getExternalAccountId()).source(a.getSource()).openedAt(a.getOpenedAt()).closedAt(a.getClosedAt()).createdAt(a.getCreatedAt()).updatedAt(a.getUpdatedAt()).build();
    }
    private TransactionResponse transactionResponse(BankTransaction t) { return TransactionResponse.builder().id(t.getId()).transactionReference(t.getTransactionReference()).bankAccountId(t.getBankAccount().getId()).transactionType(t.getTransactionType()).category(t.getCategory()).direction(t.getDirection()).amount(t.getAmount()).currency(t.getCurrency()).balanceBefore(t.getBalanceBefore()).balanceAfter(t.getBalanceAfter()).transactionDate(t.getTransactionDate()).description(t.getDescription()).referenceNumber(t.getReferenceNumber()).relatedTransferId(t.getRelatedTransfer()==null?null:t.getRelatedTransfer().getId()).referenceType(t.getReferenceType()).referenceId(t.getReferenceId()).externalTransactionId(t.getExternalTransactionId()).status(t.getStatus()).isDeleted(t.getIsDeleted()).createdAt(t.getCreatedAt()).build(); }
    private TransferResponse transferResponse(BankTransfer t) { return TransferResponse.builder().id(t.getId()).transferReference(t.getTransferReference()).sourceAccountId(t.getFromAccount().getId()).destinationAccountId(t.getToAccount().getId()).transferAmount(t.getTransferAmount()).currency(t.getCurrency()).transferDate(t.getTransferDate()).description(t.getDescription()).referenceNumber(t.getReferenceNumber()).status(t.getStatus()).sourceTransactionId(t.getSourceTransaction()==null?null:t.getSourceTransaction().getId()).destinationTransactionId(t.getDestinationTransaction()==null?null:t.getDestinationTransaction().getId()).externalTransferId(t.getExternalTransferId()).createdAt(t.getCreatedAt()).build(); }
}
