package com.example.assets_management.banking.transaction.service;

import com.example.assets_management.banking.bank_account.entity.BankAccount;
import com.example.assets_management.banking.bank_account.enums.BankAccountStatus;
import com.example.assets_management.banking.bank_account.repository.BankAccountRepository;
import com.example.assets_management.banking.transaction.dto.request.TransactionRequest;
import com.example.assets_management.banking.transaction.dto.response.TransactionResponse;
import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.banking.transaction.enums.BankTransactionCategory;
import com.example.assets_management.banking.transaction.entity.BankTransaction;
import com.example.assets_management.banking.transaction.enums.BankTransactionStatus;
import com.example.assets_management.banking.transaction.enums.BankTransactionDirection;
import com.example.assets_management.banking.transaction.enums.BankTransactionType;
import com.example.assets_management.banking.transaction.repository.BankTransactionRepository;
import com.example.assets_management.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BankingTransactionServiceImpl implements BankingTransactionService {
    private final BankTransactionRepository repository;
    private final BankAccountRepository accountRepository;

    @Override @Transactional
    public TransactionResponse credit(TransactionRequest request) { return post(request, BankTransactionDirection.CREDIT); }

    @Override @Transactional
    public TransactionResponse debit(TransactionRequest request) { return post(request, BankTransactionDirection.DEBIT); }

    private TransactionResponse post(TransactionRequest request, BankTransactionDirection direction) {
        if (request.getAmount()==null || request.getAmount().signum() <= 0) throw new IllegalArgumentException("amount must be greater than zero");
        BankAccount account = accountRepository.findByIdForUpdate(request.getBankAccountId()).orElseThrow(() -> new ResourceNotFoundException("Bank account not found: " + request.getBankAccountId()));
        if (account.getStatus()!=BankAccountStatus.ACTIVE) throw new IllegalStateException("Only ACTIVE bank accounts can perform financial operations");
        BigDecimal before = nz(account.getCurrentBalance());
        BigDecimal after = direction==BankTransactionDirection.CREDIT ? before.add(request.getAmount()) : before.subtract(request.getAmount());
        if (direction==BankTransactionDirection.DEBIT && after.signum()<0) throw new IllegalStateException("Insufficient balance");
        account.setCurrentBalance(after); accountRepository.save(account);
        BankTransaction t = new BankTransaction();
        t.setTransactionReference("TXN-"+UUID.randomUUID().toString().replace("-","").substring(0,16).toUpperCase());
        t.setBankAccount(account); t.setTransactionType(request.getTransactionType()); t.setCategory(request.getCategory()==null ? com.example.assets_management.banking.transaction.enums.BankTransactionCategory.OTHER : request.getCategory()); t.setDirection(direction); t.setAmount(request.getAmount()); t.setCurrency(request.getCurrency()==null?account.getCurrency():request.getCurrency().trim().toUpperCase()); t.setBalanceBefore(before); t.setBalanceAfter(after); t.setTransactionDate(request.getTransactionDate()==null? LocalDate.now():request.getTransactionDate()); t.setDescription(request.getDescription()); t.setReferenceNumber(blank(request.getReferenceNumber())?t.getTransactionReference():request.getReferenceNumber()); t.setReferenceType(blank(request.getReferenceType())?null:request.getReferenceType().trim().toUpperCase()); t.setReferenceId(blank(request.getReferenceId())?null:request.getReferenceId().trim()); t.setExternalTransactionId(request.getExternalTransactionId()); t.setStatus(BankTransactionStatus.COMPLETED); t.setIsDeleted(false);
        return toResponse(repository.save(t));
    }

    @Override @Transactional(readOnly=true) public List<TransactionResponse> findAll(){ return repository.findAllByIsDeletedFalseOrderByTransactionDateDescCreatedAtDesc().stream().map(this::toResponse).collect(Collectors.toList()); }
    @Override @Transactional(readOnly=true) public TransactionResponse findById(UUID id){ return toResponse(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction not found: "+id))); }
    @Override @Transactional(readOnly=true) public List<TransactionResponse> findByAccount(UUID accountId){ if(!accountRepository.existsById(accountId)) throw new ResourceNotFoundException("Bank account not found: "+accountId); return repository.findByBankAccount_IdAndIsDeletedFalseOrderByTransactionDateDescCreatedAtDesc(accountId).stream().map(this::toResponse).collect(Collectors.toList()); }

    @Override
    @Transactional
    public void processAssetPurchase(com.example.assets_management.asset_requests.entity.AssetRequest request) {
        if (request == null || request.getId() == null) {
            throw new IllegalArgumentException("Asset purchase request is required");
        }

        if (request.getPurchaseCost() == null || request.getPurchaseCost().signum() <= 0) {
            throw new IllegalArgumentException("purchaseCost must be greater than zero");
        }

        int quantity = request.getQuantity() == null ? 1 : request.getQuantity();
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be greater than zero");
        }

        BigDecimal expectedTotal = request.getPurchaseCost().multiply(BigDecimal.valueOf(quantity));
        if (request.getInvoiceAmount() == null || request.getInvoiceAmount().compareTo(expectedTotal) != 0) {
            throw new IllegalStateException("invoiceAmount must equal purchaseCost * quantity. Expected: " + expectedTotal);
        }

        if (request.getAssets() == null || request.getAssets().size() != quantity) {
            throw new IllegalStateException("Exactly " + quantity + " Asset Master record(s) must exist before payment");
        }

        if (request.getDestinationCompany() == null || request.getDestinationCompany().getId() == null) {
            throw new IllegalStateException("Destination company is required for purchase payment");
        }

        String referenceType = "ASSET_REQUEST";
        String referenceId = request.getId().toString();

        if (repository.existsByReferenceTypeAndReferenceIdAndCategoryAndStatusAndIsDeletedFalse(
                referenceType, referenceId, BankTransactionCategory.ASSET_PURCHASE, BankTransactionStatus.COMPLETED)) {
            throw new IllegalStateException("Purchase request has already been paid: " + request.getId());
        }

        UUID companyId = request.getDestinationCompany().getId();
        List<BankAccount> primaryAccounts = accountRepository.findByCompany_IdAndIsPrimaryTrueAndStatusOrderById(
                companyId, BankAccountStatus.ACTIVE);

        if (primaryAccounts.isEmpty()) {
            throw new IllegalStateException("No primary active bank account found for company");
        }
        if (primaryAccounts.size() > 1) {
            throw new IllegalStateException("Multiple primary bank accounts found for company");
        }

        BankAccount account = primaryAccounts.get(0);
        if (account.getStatus() != BankAccountStatus.ACTIVE) {
            throw new IllegalStateException("Primary bank account must be ACTIVE");
        }
        if (account.getAccountHolderType() == null ||
                account.getAccountHolderType() != com.example.assets_management.banking.bank_account.enums.BankAccountHolderType.COMPANY) {
            throw new IllegalStateException("Primary bank account must be a COMPANY account");
        }

        BigDecimal before = nz(account.getCurrentBalance());
        BigDecimal amount = expectedTotal;
        if (before.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient bank account balance for asset purchase. Required: " + amount + ", available: " + before);
        }

        BigDecimal after = before.subtract(amount);
        account.setCurrentBalance(after);
        accountRepository.save(account);

        BankTransaction transaction = new BankTransaction();
        transaction.setTransactionReference("TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());
        transaction.setBankAccount(account);
        transaction.setTransactionType(BankTransactionType.PAYMENT);
        transaction.setCategory(BankTransactionCategory.ASSET_PURCHASE);
        transaction.setDirection(BankTransactionDirection.DEBIT);
        transaction.setAmount(amount);
        transaction.setCurrency(account.getCurrency() == null ? "INR" : account.getCurrency().trim().toUpperCase());
        transaction.setBalanceBefore(before);
        transaction.setBalanceAfter(after);
        transaction.setTransactionDate(request.getRequestDate() == null ? LocalDate.now() : request.getRequestDate());
        transaction.setDescription("Asset purchase request #" + request.getId() + " - " + quantity + " asset(s)");
        transaction.setReferenceNumber(transaction.getTransactionReference());
        transaction.setReferenceType(referenceType);
        transaction.setReferenceId(referenceId);
        transaction.setStatus(BankTransactionStatus.COMPLETED);
        transaction.setIsDeleted(false);

        repository.save(transaction);
    }

    @Override @Transactional
    public TransactionResponse reverse(UUID id) {
        BankTransaction original=repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction not found: "+id));
        if(original.getStatus()!=BankTransactionStatus.COMPLETED) throw new IllegalStateException("Only COMPLETED transactions can be reversed");
        if(original.getRelatedTransfer()!=null) throw new IllegalStateException("Transfer-generated transactions must be reversed through the transfer operation");
        BankAccount account=accountRepository.findByIdForUpdate(original.getBankAccount().getId()).orElseThrow(() -> new ResourceNotFoundException("Bank account not found: "+original.getBankAccount().getId()));
        if(account.getStatus()!=BankAccountStatus.ACTIVE) throw new IllegalStateException("Only ACTIVE bank accounts can reverse transactions");
        BigDecimal before=nz(account.getCurrentBalance());
        BigDecimal after=original.getDirection()==BankTransactionDirection.CREDIT?before.subtract(original.getAmount()):before.add(original.getAmount());
        if(after.signum()<0) throw new IllegalStateException("Reversal would create a negative balance");
        account.setCurrentBalance(after); accountRepository.save(account);
        original.setStatus(BankTransactionStatus.REVERSED); repository.save(original);
        BankTransaction reversal=new BankTransaction(); reversal.setTransactionReference("TXN-REV-"+UUID.randomUUID().toString().replace("-","").substring(0,12).toUpperCase()); reversal.setBankAccount(account); reversal.setTransactionType(BankTransactionType.ADJUSTMENT); reversal.setCategory(original.getCategory()); reversal.setDirection(original.getDirection()==BankTransactionDirection.CREDIT?BankTransactionDirection.DEBIT:BankTransactionDirection.CREDIT); reversal.setAmount(original.getAmount()); reversal.setCurrency(original.getCurrency()); reversal.setBalanceBefore(before); reversal.setBalanceAfter(after); reversal.setTransactionDate(LocalDate.now()); reversal.setDescription("Reversal of "+original.getTransactionReference()); reversal.setReferenceNumber(reversal.getTransactionReference()); reversal.setReferenceType("TRANSACTION_REVERSAL"); reversal.setReferenceId(original.getId().toString()); reversal.setStatus(BankTransactionStatus.COMPLETED); reversal.setIsDeleted(false);
        return toResponse(repository.save(reversal));
    }
    private TransactionResponse toResponse(BankTransaction t){ return TransactionResponse.builder().id(t.getId()).transactionReference(t.getTransactionReference()).bankAccountId(t.getBankAccount().getId()).transactionType(t.getTransactionType()).category(t.getCategory()).direction(t.getDirection()).amount(t.getAmount()).currency(t.getCurrency()).balanceBefore(t.getBalanceBefore()).balanceAfter(t.getBalanceAfter()).transactionDate(t.getTransactionDate()).description(t.getDescription()).referenceNumber(t.getReferenceNumber()).relatedTransferId(t.getRelatedTransfer()==null?null:t.getRelatedTransfer().getId()).referenceType(t.getReferenceType()).referenceId(t.getReferenceId()).externalTransactionId(t.getExternalTransactionId()).status(t.getStatus()).isDeleted(t.getIsDeleted()).createdAt(t.getCreatedAt()).build(); }
    private BigDecimal nz(BigDecimal x){return x==null?BigDecimal.ZERO:x;}
    private boolean blank(String x){return x==null||x.isBlank();}
}

/*
package com.example.assets_management.banking.transaction.service;

import com.example.assets_management.banking.bank_account.entity.BankAccount;
import com.example.assets_management.banking.bank_account.enums.BankAccountStatus;
import com.example.assets_management.banking.bank_account.repository.BankAccountRepository;
import com.example.assets_management.banking.transaction.dto.request.TransactionRequest;
import com.example.assets_management.banking.transaction.dto.response.TransactionResponse;
import com.example.assets_management.asset_master.entity.Asset;
import com.example.assets_management.banking.transaction.enums.BankTransactionCategory;
import com.example.assets_management.banking.transaction.entity.BankTransaction;
import com.example.assets_management.banking.transaction.enums.BankTransactionStatus;
import com.example.assets_management.banking.transaction.enums.BankTransactionDirection;
import com.example.assets_management.banking.transaction.enums.BankTransactionType;
import com.example.assets_management.banking.transaction.repository.BankTransactionRepository;
import com.example.assets_management.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BankingTransactionServiceImpl implements BankingTransactionService {
    private final BankTransactionRepository repository;
    private final BankAccountRepository accountRepository;

    @Override @Transactional
    public TransactionResponse credit(TransactionRequest request) { return post(request, BankTransactionDirection.CREDIT); }

    @Override @Transactional
    public TransactionResponse debit(TransactionRequest request) { return post(request, BankTransactionDirection.DEBIT); }

    private TransactionResponse post(TransactionRequest request, BankTransactionDirection direction) {
        if (request.getAmount()==null || request.getAmount().signum() <= 0) throw new IllegalArgumentException("amount must be greater than zero");
        BankAccount account = accountRepository.findByIdForUpdate(request.getBankAccountId()).orElseThrow(() -> new ResourceNotFoundException("Bank account not found: " + request.getBankAccountId()));
        if (account.getStatus()!=BankAccountStatus.ACTIVE) throw new IllegalStateException("Only ACTIVE bank accounts can perform financial operations");
        BigDecimal before = nz(account.getCurrentBalance());
        BigDecimal after = direction==BankTransactionDirection.CREDIT ? before.add(request.getAmount()) : before.subtract(request.getAmount());
        if (direction==BankTransactionDirection.DEBIT && after.signum()<0) throw new IllegalStateException("Insufficient balance");
        account.setCurrentBalance(after); accountRepository.save(account);
        BankTransaction t = new BankTransaction();
        t.setTransactionReference("TXN-"+UUID.randomUUID().toString().replace("-","").substring(0,16).toUpperCase());
        t.setBankAccount(account); t.setTransactionType(request.getTransactionType()); t.setCategory(request.getCategory()==null ? com.example.assets_management.banking.transaction.enums.BankTransactionCategory.OTHER : request.getCategory()); t.setDirection(direction); t.setAmount(request.getAmount()); t.setCurrency(request.getCurrency()==null?account.getCurrency():request.getCurrency().trim().toUpperCase()); t.setBalanceBefore(before); t.setBalanceAfter(after); t.setTransactionDate(request.getTransactionDate()==null? LocalDate.now():request.getTransactionDate()); t.setDescription(request.getDescription()); t.setReferenceNumber(blank(request.getReferenceNumber())?t.getTransactionReference():request.getReferenceNumber()); t.setReferenceType(blank(request.getReferenceType())?null:request.getReferenceType().trim().toUpperCase()); t.setReferenceId(blank(request.getReferenceId())?null:request.getReferenceId().trim()); t.setExternalTransactionId(request.getExternalTransactionId()); t.setStatus(BankTransactionStatus.COMPLETED); t.setIsDeleted(false);
        return toResponse(repository.save(t));
    }

    @Override @Transactional(readOnly=true) public List<TransactionResponse> findAll(){ return repository.findAllByIsDeletedFalseOrderByTransactionDateDescCreatedAtDesc().stream().map(this::toResponse).collect(Collectors.toList()); }
    @Override @Transactional(readOnly=true) public TransactionResponse findById(UUID id){ return toResponse(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction not found: "+id))); }
    @Override @Transactional(readOnly=true) public List<TransactionResponse> findByAccount(UUID accountId){ if(!accountRepository.existsById(accountId)) throw new ResourceNotFoundException("Bank account not found: "+accountId); return repository.findByBankAccount_IdAndIsDeletedFalseOrderByTransactionDateDescCreatedAtDesc(accountId).stream().map(this::toResponse).collect(Collectors.toList()); }

    @Override
    @Transactional
    public void processAssetPurchase(Asset asset) {
        if (asset == null || asset.getId() == null) {
            throw new IllegalArgumentException("Asset is required for purchase processing");
        }

        if (asset.getPurchaseCost() == null || asset.getPurchaseCost().signum() <= 0) {
            throw new IllegalArgumentException("Asset purchase cost must be greater than zero");
        }

        if (asset.getCompany() == null || asset.getCompany().getId() == null) {
            throw new IllegalStateException("Asset company is required for purchase processing");
        }

        UUID companyId = asset.getCompany().getId();
        String assetReferenceId = asset.getId().toString();
        if (repository.existsByReferenceTypeAndReferenceIdAndCategoryAndStatusAndIsDeletedFalse(
                "ASSET", assetReferenceId, BankTransactionCategory.ASSET_PURCHASE, BankTransactionStatus.COMPLETED)) {
            throw new IllegalStateException("Asset purchase has already been paid for asset: " + assetReferenceId);
        }

        List<BankAccount> primaryAccounts = accountRepository.findByCompany_IdAndIsPrimaryTrueAndStatusOrderById(companyId, BankAccountStatus.ACTIVE);

        if (primaryAccounts.isEmpty()) {
            throw new IllegalStateException("No primary active bank account found for company");
        }
        if (primaryAccounts.size() > 1) {
            throw new IllegalStateException("Multiple primary bank accounts found for company");
        }

        BankAccount account = primaryAccounts.get(0);
        if (account.getStatus() != BankAccountStatus.ACTIVE) {
            throw new IllegalStateException("Primary bank account must be ACTIVE");
        }
        if (account.getAccountHolderType() == null ||
                account.getAccountHolderType() != com.example.assets_management.banking.bank_account.enums.BankAccountHolderType.COMPANY) {
            throw new IllegalStateException("Primary bank account must be a COMPANY account");
        }

        BigDecimal before = nz(account.getCurrentBalance());
        BigDecimal amount = asset.getPurchaseCost();
        if (before.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient bank account balance for asset purchase");
        }

        BigDecimal after = before.subtract(amount);
        account.setCurrentBalance(after);
        accountRepository.save(account);

        BankTransaction transaction = new BankTransaction();
        transaction.setTransactionReference("TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase());
        transaction.setBankAccount(account);
        transaction.setTransactionType(BankTransactionType.PAYMENT);
        transaction.setCategory(BankTransactionCategory.ASSET_PURCHASE);
        transaction.setDirection(BankTransactionDirection.DEBIT);
        transaction.setAmount(amount);
        transaction.setCurrency(account.getCurrency() == null ? "INR" : account.getCurrency().trim().toUpperCase());
        transaction.setBalanceBefore(before);
        transaction.setBalanceAfter(after);
        transaction.setTransactionDate(asset.getPurchaseDate() == null ? LocalDate.now() : asset.getPurchaseDate());
        transaction.setDescription("Asset purchase: " + asset.getName());
        transaction.setReferenceNumber(transaction.getTransactionReference());
        transaction.setReferenceType("ASSET");
        transaction.setReferenceId(assetReferenceId);
        transaction.setStatus(BankTransactionStatus.COMPLETED);
        transaction.setIsDeleted(false);

        repository.save(transaction);
    }

    @Override @Transactional
    public TransactionResponse reverse(UUID id) {
        BankTransaction original=repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction not found: "+id));
        if(original.getStatus()!=BankTransactionStatus.COMPLETED) throw new IllegalStateException("Only COMPLETED transactions can be reversed");
        if(original.getRelatedTransfer()!=null) throw new IllegalStateException("Transfer-generated transactions must be reversed through the transfer operation");
        BankAccount account=accountRepository.findByIdForUpdate(original.getBankAccount().getId()).orElseThrow(() -> new ResourceNotFoundException("Bank account not found: "+original.getBankAccount().getId()));
        if(account.getStatus()!=BankAccountStatus.ACTIVE) throw new IllegalStateException("Only ACTIVE bank accounts can reverse transactions");
        BigDecimal before=nz(account.getCurrentBalance());
        BigDecimal after=original.getDirection()==BankTransactionDirection.CREDIT?before.subtract(original.getAmount()):before.add(original.getAmount());
        if(after.signum()<0) throw new IllegalStateException("Reversal would create a negative balance");
        account.setCurrentBalance(after); accountRepository.save(account);
        original.setStatus(BankTransactionStatus.REVERSED); repository.save(original);
        BankTransaction reversal=new BankTransaction(); reversal.setTransactionReference("TXN-REV-"+UUID.randomUUID().toString().replace("-","").substring(0,12).toUpperCase()); reversal.setBankAccount(account); reversal.setTransactionType(BankTransactionType.ADJUSTMENT); reversal.setCategory(original.getCategory()); reversal.setDirection(original.getDirection()==BankTransactionDirection.CREDIT?BankTransactionDirection.DEBIT:BankTransactionDirection.CREDIT); reversal.setAmount(original.getAmount()); reversal.setCurrency(original.getCurrency()); reversal.setBalanceBefore(before); reversal.setBalanceAfter(after); reversal.setTransactionDate(LocalDate.now()); reversal.setDescription("Reversal of "+original.getTransactionReference()); reversal.setReferenceNumber(reversal.getTransactionReference()); reversal.setReferenceType("TRANSACTION_REVERSAL"); reversal.setReferenceId(original.getId().toString()); reversal.setStatus(BankTransactionStatus.COMPLETED); reversal.setIsDeleted(false);
        return toResponse(repository.save(reversal));
    }
    private TransactionResponse toResponse(BankTransaction t){ return TransactionResponse.builder().id(t.getId()).transactionReference(t.getTransactionReference()).bankAccountId(t.getBankAccount().getId()).transactionType(t.getTransactionType()).category(t.getCategory()).direction(t.getDirection()).amount(t.getAmount()).currency(t.getCurrency()).balanceBefore(t.getBalanceBefore()).balanceAfter(t.getBalanceAfter()).transactionDate(t.getTransactionDate()).description(t.getDescription()).referenceNumber(t.getReferenceNumber()).relatedTransferId(t.getRelatedTransfer()==null?null:t.getRelatedTransfer().getId()).referenceType(t.getReferenceType()).referenceId(t.getReferenceId()).externalTransactionId(t.getExternalTransactionId()).status(t.getStatus()).isDeleted(t.getIsDeleted()).createdAt(t.getCreatedAt()).build(); }
    private BigDecimal nz(BigDecimal x){return x==null?BigDecimal.ZERO:x;}
    private boolean blank(String x){return x==null||x.isBlank();}
}
*/