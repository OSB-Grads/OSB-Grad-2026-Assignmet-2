package com.bank.server.service;

import com.bank.server.dto.TransactionDTO;
import com.bank.server.entity.Transaction;
import com.bank.server.exception.TransactionNotFoundException;
import com.bank.server.mapper.TransactionMapper;
import com.bank.server.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<TransactionDTO> getTransactionsByAccountId(String accountId) {

        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);

        List<TransactionDTO> dtoList = new ArrayList<>();

        for (Transaction transaction : transactions) {
            dtoList.add(transactionMapper.toDto(transaction));
        }

        return dtoList;
    }
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<TransactionDTO> getTransactionsByCustomerId(String customerId) {

        List<Transaction> transactions = transactionRepository.findByCustomerId(customerId);

        List<TransactionDTO> dtoList = new ArrayList<>();

        for (Transaction transaction : transactions) {
            dtoList.add(transactionMapper.toDto(transaction));
        }
        return dtoList;
    }
}