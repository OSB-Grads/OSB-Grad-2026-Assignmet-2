package com.bank.server.service;

import com.bank.server.dto.TransactionDTO;
import com.bank.server.entity.Account;
import com.bank.server.entity.Customer;
import com.bank.server.entity.Transaction;
import com.bank.server.exception.AccountNotFoundException;
import com.bank.server.exception.CustomerNotFoundException;
import com.bank.server.exception.TransactionNotFoundException;
import com.bank.server.mapper.TransactionMapper;
import com.bank.server.repository.AccountRepository;
import com.bank.server.repository.CustomerRepository;
import com.bank.server.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    public TransactionDTO createTransaction(TransactionDTO transactionDTO)
    {
        Transaction transaction = transactionMapper.toEntity(transactionDTO);

        transaction.setId(UUID.randomUUID().toString());

        Customer customer = customerRepository.findById(transactionDTO.getCustomerId())
                .orElseThrow(()->new CustomerNotFoundException("CUTSOMER_NOT_FOUND","Customer not found for transaction creation"));

        transaction.setCustomer(customer);

        if(transactionDTO.getFromAccountId()!=null)
        {
            Account account = accountRepository.findById(transactionDTO.getFromAccountId())
                    .orElseThrow(()->new AccountNotFoundException("ACCOUNT_NOT_FOUND","Account not found for transaction creation"));
            transaction.setFromAccount(account);
        }

        if(transactionDTO.getToAccountId()!=null)
        {
            Account account = accountRepository.findById(transactionDTO.getToAccountId())
                    .orElseThrow(()->new AccountNotFoundException("ACCOUNT_NOT_FOUND","Account not found for transaction creation"));
            transaction.setToAccount(account);
        }

        Transaction savedTransaction = transactionRepository.save(transaction);

        return transactionMapper.toDto(savedTransaction);
    }
}