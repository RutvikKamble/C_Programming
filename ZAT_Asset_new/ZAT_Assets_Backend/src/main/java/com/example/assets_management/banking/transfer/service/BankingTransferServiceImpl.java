package com.example.assets_management.banking.transfer.service;

import com.example.assets_management.banking.bank_account.entity.BankAccount;
import com.example.assets_management.banking.bank_account.enums.BankAccountStatus;
import com.example.assets_management.banking.bank_account.repository.BankAccountRepository;
import com.example.assets_management.banking.transaction.entity.BankTransaction;
import com.example.assets_management.banking.transaction.enums.BankTransactionCategory;
import com.example.assets_management.banking.transaction.enums.BankTransactionDirection;
import com.example.assets_management.banking.transaction.enums.BankTransactionStatus;
import com.example.assets_management.banking.transaction.enums.BankTransactionType;
import com.example.assets_management.banking.transaction.repository.BankTransactionRepository;
import com.example.assets_management.banking.transfer.dto.request.TransferRequest;
import com.example.assets_management.banking.transfer.dto.response.TransferResponse;
import com.example.assets_management.banking.transfer.entity.BankTransfer;
import com.example.assets_management.banking.transfer.enums.BankTransferStatus;
import com.example.assets_management.banking.transfer.repository.BankTransferRepository;
import com.example.assets_management.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BankingTransferServiceImpl implements BankingTransferService {
    private final BankTransferRepository transferRepository;
    private final BankAccountRepository accountRepository;
    private final BankTransactionRepository transactionRepository;

    @Override @Transactional
    public TransferResponse transfer(TransferRequest request) {
        if(request.getTransferAmount()==null || request.getTransferAmount().signum()<=0) throw new IllegalArgumentException("transfer_amount must be greater than zero");
        if(request.getSourceAccountId().equals(request.getDestinationAccountId())) throw new IllegalArgumentException("Source and destination accounts must be different");
        List<UUID> ids=List.of(request.getSourceAccountId(),request.getDestinationAccountId()).stream().sorted(Comparator.comparing(UUID::toString)).collect(Collectors.toList());
        BankAccount first=accountRepository.findByIdForUpdate(ids.get(0)).orElseThrow(() -> new ResourceNotFoundException("Bank account not found: "+ids.get(0)));
        BankAccount second=accountRepository.findByIdForUpdate(ids.get(1)).orElseThrow(() -> new ResourceNotFoundException("Bank account not found: "+ids.get(1)));
        BankAccount source=first.getId().equals(request.getSourceAccountId())?first:second;
        BankAccount destination=first.getId().equals(request.getDestinationAccountId())?first:second;
        if(source.getStatus()!=BankAccountStatus.ACTIVE || destination.getStatus()!=BankAccountStatus.ACTIVE) throw new IllegalStateException("Both accounts must be ACTIVE");
        if(!source.getCompany().getId().equals(destination.getCompany().getId())) throw new IllegalArgumentException("Internal transfer requires both accounts to belong to the same company");
        if(!source.getCurrency().equalsIgnoreCase(destination.getCurrency())) throw new IllegalArgumentException("Source and destination currencies must match");
        if(request.getCurrency()!=null && !request.getCurrency().equalsIgnoreCase(source.getCurrency())) throw new IllegalArgumentException("Transfer currency must match account currency");
        BigDecimal amount=request.getTransferAmount(); BigDecimal sourceBefore=nz(source.getCurrentBalance()); BigDecimal destBefore=nz(destination.getCurrentBalance()); BigDecimal sourceAfter=sourceBefore.subtract(amount); BigDecimal destAfter=destBefore.add(amount);
        if(sourceAfter.signum()<0) throw new IllegalStateException("Insufficient balance");
        BankTransfer transfer=new BankTransfer(); transfer.setTransferReference("TRF-"+UUID.randomUUID().toString().replace("-","").substring(0,16).toUpperCase()); transfer.setFromAccount(source); transfer.setToAccount(destination); transfer.setTransferAmount(amount); transfer.setCurrency(source.getCurrency()); transfer.setTransferDate(request.getTransferDate()==null?LocalDate.now():request.getTransferDate()); transfer.setDescription(request.getDescription()); transfer.setReferenceNumber(blank(request.getReferenceNumber())?transfer.getTransferReference():request.getReferenceNumber()); transfer.setStatus(BankTransferStatus.PENDING); transfer=transferRepository.saveAndFlush(transfer);
        source.setCurrentBalance(sourceAfter); destination.setCurrentBalance(destAfter); accountRepository.save(source); accountRepository.save(destination);
        BankTransaction debit=buildTransaction(source, BankTransactionDirection.DEBIT, amount, sourceBefore, sourceAfter, transfer, "TRANSFER");
        BankTransaction credit=buildTransaction(destination, BankTransactionDirection.CREDIT, amount, destBefore, destAfter, transfer, "TRANSFER");
        debit=transactionRepository.save(debit); credit=transactionRepository.save(credit);
        transfer.setSourceTransaction(debit); transfer.setDestinationTransaction(credit); transfer.setStatus(BankTransferStatus.COMPLETED); transfer=transferRepository.save(transfer);
        return toResponse(transfer);
    }

    private BankTransaction buildTransaction(BankAccount account, BankTransactionDirection direction, BigDecimal amount, BigDecimal before, BigDecimal after, BankTransfer transfer, String refType){
        BankTransaction t=new BankTransaction(); t.setTransactionReference("TXN-"+UUID.randomUUID().toString().replace("-","").substring(0,16).toUpperCase()); t.setBankAccount(account); t.setTransactionType(direction==BankTransactionDirection.DEBIT?BankTransactionType.PAYMENT:BankTransactionType.RECEIPT); t.setCategory(BankTransactionCategory.OTHER); t.setDirection(direction); t.setAmount(amount); t.setCurrency(account.getCurrency()); t.setBalanceBefore(before); t.setBalanceAfter(after); t.setTransactionDate(transfer.getTransferDate()); t.setDescription("Bank transfer "+transfer.getTransferReference()); t.setReferenceNumber(t.getTransactionReference()); t.setRelatedTransfer(transfer); t.setReferenceType(refType); t.setReferenceId(transfer.getId().toString()); t.setStatus(BankTransactionStatus.COMPLETED); t.setIsDeleted(false); return t;
    }

    @Override @Transactional(readOnly=true) public List<TransferResponse> findAll(){return transferRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());}
    @Override @Transactional(readOnly=true) public TransferResponse findById(UUID id){return toResponse(transferRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Bank transfer not found: "+id)));}
    @Override @Transactional(readOnly=true) public List<TransferResponse> findByAccount(UUID id){if(!accountRepository.existsById(id))throw new ResourceNotFoundException("Bank account not found: "+id);return transferRepository.findByFromAccount_IdOrToAccount_IdOrderByTransferDateDescCreatedAtDesc(id,id).stream().map(this::toResponse).collect(Collectors.toList());}

    @Override @Transactional
    public TransferResponse reverse(UUID id){
        BankTransfer original=transferRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Bank transfer not found: "+id));
        if(original.getStatus()!=BankTransferStatus.COMPLETED) throw new IllegalStateException("Only COMPLETED transfers can be reversed");
        List<UUID> ids=List.of(original.getFromAccount().getId(),original.getToAccount().getId()).stream().sorted(Comparator.comparing(UUID::toString)).collect(Collectors.toList());
        BankAccount first=accountRepository.findByIdForUpdate(ids.get(0)).orElseThrow(); BankAccount second=accountRepository.findByIdForUpdate(ids.get(1)).orElseThrow(); BankAccount source=first.getId().equals(original.getFromAccount().getId())?first:second; BankAccount destination=first.getId().equals(original.getToAccount().getId())?first:second;
        if(source.getStatus()!=BankAccountStatus.ACTIVE || destination.getStatus()!=BankAccountStatus.ACTIVE) throw new IllegalStateException("Both accounts must be ACTIVE for reversal");
        BigDecimal sourceBefore=nz(source.getCurrentBalance()), destBefore=nz(destination.getCurrentBalance()); BigDecimal sourceAfter=sourceBefore.add(original.getTransferAmount()), destAfter=destBefore.subtract(original.getTransferAmount()); if(destAfter.signum()<0) throw new IllegalStateException("Transfer reversal would create a negative destination balance");
        source.setCurrentBalance(sourceAfter); destination.setCurrentBalance(destAfter); accountRepository.save(source); accountRepository.save(destination);
        original.setStatus(BankTransferStatus.REVERSED); transferRepository.save(original);
        BankTransfer reversal=new BankTransfer(); reversal.setTransferReference("TRF-REV-"+UUID.randomUUID().toString().replace("-","").substring(0,12).toUpperCase()); reversal.setFromAccount(destination); reversal.setToAccount(source); reversal.setTransferAmount(original.getTransferAmount()); reversal.setCurrency(original.getCurrency()); reversal.setTransferDate(LocalDate.now()); reversal.setDescription("Reversal of "+original.getTransferReference()); reversal.setReferenceNumber(reversal.getTransferReference()); reversal.setStatus(BankTransferStatus.COMPLETED); reversal=transferRepository.saveAndFlush(reversal);
        BankTransaction debit=buildTransaction(destination,BankTransactionDirection.DEBIT,original.getTransferAmount(),destBefore,destAfter,reversal,"TRANSFER_REVERSAL"); BankTransaction credit=buildTransaction(source,BankTransactionDirection.CREDIT,original.getTransferAmount(),sourceBefore,sourceAfter,reversal,"TRANSFER_REVERSAL"); debit=transactionRepository.save(debit); credit=transactionRepository.save(credit); reversal.setSourceTransaction(debit); reversal.setDestinationTransaction(credit); return toResponse(transferRepository.save(reversal));
    }
    private TransferResponse toResponse(BankTransfer t){return TransferResponse.builder().id(t.getId()).transferReference(t.getTransferReference()).sourceAccountId(t.getFromAccount().getId()).destinationAccountId(t.getToAccount().getId()).transferAmount(t.getTransferAmount()).currency(t.getCurrency()).transferDate(t.getTransferDate()).description(t.getDescription()).referenceNumber(t.getReferenceNumber()).status(t.getStatus()).sourceTransactionId(t.getSourceTransaction()==null?null:t.getSourceTransaction().getId()).destinationTransactionId(t.getDestinationTransaction()==null?null:t.getDestinationTransaction().getId()).externalTransferId(t.getExternalTransferId()).createdAt(t.getCreatedAt()).build();}
    private BigDecimal nz(BigDecimal x){return x==null?BigDecimal.ZERO:x;} private boolean blank(String x){return x==null||x.isBlank();}
}
