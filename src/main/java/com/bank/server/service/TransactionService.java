package com.bank.server.service;

import com.bank.server.dto.TransactionDTO;
import com.bank.server.entity.Transaction;
import com.bank.server.exception.TransactionNotFoundException;
import com.bank.server.mapper.TransactionMapper;
import com.bank.server.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public TransactionService(TransactionRepository transactionRepository,
                              TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }

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
}