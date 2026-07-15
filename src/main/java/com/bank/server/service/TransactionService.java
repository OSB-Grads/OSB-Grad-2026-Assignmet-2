package com.bank.server.service;

import com.bank.server.dto.TransactionDTO;
import com.bank.server.dto.response.TransferResponse;
import com.bank.server.entity.Account;
import com.bank.server.entity.Transaction;
import com.bank.server.exception.TransactionNotFoundException;
import com.bank.server.mapper.TransactionMapper;
import com.bank.server.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public TransactionDTO getTransactionById(String id) {

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        return transactionMapper.toDto(transaction);
    }

    public List<TransactionDTO> getTransactionsByAccountId(String accountId) {

        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);

        List<TransactionDTO> dtoList = new ArrayList<>();

        for (Transaction transaction : transactions) {
            dtoList.add(transactionMapper.toDto(transaction));
        }

        return dtoList;
    }
    public List<TransactionDTO> getTransactionsByCustomerId(String customerId) {

        List<Transaction> transactions = transactionRepository.findByCustomerId(customerId);

        List<TransactionDTO> dtoList = new ArrayList<>();

        for (Transaction transaction : transactions) {
            dtoList.add(transactionMapper.toDto(transaction));
        }
        return dtoList;
    }
    public Transaction createTransferTransaction(
            Account sourceAccount,
            Account destinationAccount,
            BigDecimal amount
    ) {
        String reference = UUID.randomUUID().toString();

        Transaction transaction = new Transaction();
        transaction.setId(reference);

        transaction.setTransactionType("TRANSFER");
        transaction.setAmount(amount);
        transaction.setStatus("COMPLETED");

        transaction.setDescription(
                "Internal transfer reference: " + reference
        );
        transaction.setCustomer(sourceAccount.getCustomer());
        transaction.setFromAccount(sourceAccount);
        transaction.setToAccount(destinationAccount);

        return transactionRepository.save(transaction);
    }
    public TransferResponse toTransferResponse(
            Transaction transaction,
            Account sourceAccount,
            Account destinationAccount
    ) {
        return TransferResponse.builder()
                .transferReference(transaction.getId())
                .sourceAccountNumber(
                        sourceAccount.getAccountNumber()
                )
                .destinationAccountNumber(
                        destinationAccount.getAccountNumber()
                )
                .amount(transaction.getAmount())
                .sourceBalance(sourceAccount.getBalance())
                .destinationBalance(
                        destinationAccount.getBalance()
                )
                .status(transaction.getStatus())
                .transferredAt(transaction.getCreatedAt())
                .message("Transfer completed successfully")
                .build();
    }
}