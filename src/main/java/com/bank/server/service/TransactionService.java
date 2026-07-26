package com.bank.server.service;

import com.bank.server.dto.TransactionDTO;
import com.bank.server.dto.response.PaymentResponseDTO;
import com.bank.server.dto.response.TransferResponse;
import com.bank.server.entity.Account;
import com.bank.server.entity.Customer;
import com.bank.server.entity.Transaction;
import com.bank.server.enums.TransactionStatus;
import com.bank.server.exception.AccountNotFoundException;
import com.bank.server.exception.CustomerNotFoundException;
import com.bank.server.exception.TransactionNotFoundException;
import com.bank.server.mapper.TransactionMapper;
import com.bank.server.repository.AccountRepository;
import com.bank.server.repository.CustomerRepository;
import com.bank.server.repository.TransactionRepository;
import com.bank.server.utils.Generator;
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
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public TransactionDTO getTransactionById(String id) {

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(
                        "Transaction_Not_Found",
                        "Transaction not found with id: " + id
                ));

        return transactionMapper.toDto(transaction);
    }

    public List<TransactionDTO> getTransactionsByAccountNumber(String accountNumber) {

        Optional<Account> account =
                accountRepository.findByAccountNumber(accountNumber);

        String accountId = account.get().getId();

        List<Transaction> transactions =
                transactionRepository.findByAccountId(accountId);

        List<TransactionDTO> dtoList = new ArrayList<>();

        for (Transaction transaction : transactions) {
            dtoList.add(transactionMapper.toDto(transaction));
        }

        return dtoList;
    }

    public List<TransactionDTO> getTransactionsByAccountId(String accountId) {

        accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "ACCOUNT_NOT_FOUND",
                                "Account not found"));

        List<Transaction> transactions =
                transactionRepository.findByAccountId(accountId);

        List<TransactionDTO> dtoList = new ArrayList<>();

        for (Transaction transaction : transactions) {
            dtoList.add(transactionMapper.toDto(transaction));
        }

        return dtoList;
    }

    public List<TransactionDTO> getTransactionsByCustomerId(String customerId) {

        List<Transaction> transactions =
                transactionRepository.findByCustomerId(customerId);

        List<TransactionDTO> dtoList = new ArrayList<>();

        for (Transaction transaction : transactions) {
            dtoList.add(transactionMapper.toDto(transaction));
        }

        return dtoList;
    }

    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {

        Transaction transaction =
                transactionMapper.toEntity(transactionDTO);

        transaction.setId(Generator.generateUuid());

        Customer customer = customerRepository
                .findById(transactionDTO.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(
                        "CUTSOMER_NOT_FOUND",
                        "Customer not found for transaction creation"
                ));

        transaction.setCustomer(customer);

        if (transactionDTO.getFromAccountId() != null) {

            Account account = accountRepository
                    .findById(transactionDTO.getFromAccountId())
                    .orElseThrow(() -> new AccountNotFoundException(
                            "ACCOUNT_NOT_FOUND",
                            "Account not found for transaction creation"
                    ));

            transaction.setFromAccount(account);
        }

        if (transactionDTO.getToAccountId() != null) {

            Account account = accountRepository
                    .findById(transactionDTO.getToAccountId())
                    .orElseThrow(() -> new AccountNotFoundException(
                            "ACCOUNT_NOT_FOUND",
                            "Account not found for transaction creation"
                    ));

            transaction.setToAccount(account);
        }

        Transaction savedTransaction =
                transactionRepository.save(transaction);

        return transactionMapper.toDto(savedTransaction);
    }

    public TransactionDTO updateTransaction(
            String transactionId,
            TransactionStatus status) {

        Transaction transaction = transactionRepository
                .findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(
                        "TRANSACTION_NOT_FOUND",
                        "Transaction not found for id " + transactionId
                ));

        transaction.setStatus(status);

        Transaction updatedTransaction =
                transactionRepository.save(transaction);

        return transactionMapper.toDto(updatedTransaction);
    }

    public PaymentResponseDTO getTransactionStatus(String transactionId) {

        Transaction transaction = transactionRepository
                .findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(
                        "TRANSACTION_NOT_FOUND",
                        "Transaction not found for id " + transactionId
                ));

        return PaymentResponseDTO.builder()
                .id(transaction.getId())
                .status(transaction.getStatus())
                .message("Payment status fetched successfully.")
                .build();
    }

    public Transaction createTransferTransaction(
            Account sourceAccount,
            Account destinationAccount,
            BigDecimal amount) {

        String reference = UUID.randomUUID().toString();

        Transaction transaction = new Transaction();

        transaction.setId(reference);
        transaction.setTransactionType("TRANSFER");
        transaction.setAmount(amount);
        transaction.setStatus(TransactionStatus.COMPLETED);

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
            Account destinationAccount) {

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