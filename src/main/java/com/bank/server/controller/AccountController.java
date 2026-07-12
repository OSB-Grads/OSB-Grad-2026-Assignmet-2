package com.bank.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.bank.server.dto.AccountDTO;
import com.bank.server.service.AccountsService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountsService accountsService;

    public AccountController(AccountsService accountsService) {
        this.accountsService = accountsService;
    }

    @PostMapping
    public ResponseEntity<String> createAccount(@RequestBody AccountDTO accountDto) {
        String accountNumber = accountsService.createAccount(accountDto.getCustomerId(), accountDto.getProductId());
        return ResponseEntity.status(HttpStatus.CREATED).body(accountNumber);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getCustomerPortfolio(
            @RequestAttribute("currentCustomerId") String customerId) {

        List<Map<String, Object>> portfolio = accountsService.getAllAccountsForCustomer(customerId);
        return ResponseEntity.ok(portfolio);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<List<Map<String, Object>>> getAccountDetails(
            @RequestAttribute("currentCustomerId") String customerId,
            @PathVariable("accountNumber") String accountNumber) {
        List<Map<String, Object>> accounts = accountsService.getAllAccountsForCustomer(customerId)
                .stream()
                .filter(account -> account.get("accountNumber").equals(accountNumber))
                .toList();
        return ResponseEntity.ok(accounts);
    }

    // @GetMapping("/{accountId}/transactions")
    // public ResponseEntity<List<Object>> getAccountLedger(@PathVariable("accountId") String accountId) {
    //     List<TransactionDto> transactions = transactionService.listAccountTransactions(accountId);
    //     return ResponseEntity.ok(transactions);
    // }
}