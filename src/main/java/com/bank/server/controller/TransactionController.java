package com.bank.server.controller;

import com.bank.server.dto.TransactionDTO;
import com.bank.server.service.TransactionService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getTransactionById(
            @PathVariable String id) {

        return ResponseEntity.ok(
                transactionService.getTransactionById(id));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByAccountId(
            @PathVariable String accountId) {

        return ResponseEntity.ok(
                transactionService.getTransactionsByAccountNumber(accountId));
    }
}