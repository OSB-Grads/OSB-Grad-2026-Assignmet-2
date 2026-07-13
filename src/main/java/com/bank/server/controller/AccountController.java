package com.bank.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.bank.server.dto.AccountDTO;
import com.bank.server.entity.Account;
import com.bank.server.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountsService;

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@Valid @RequestBody AccountDTO accountDto) {
        AccountDTO account = accountsService.createAccount(accountDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    @GetMapping
    public ResponseEntity<List<Account>> getCustomerPortfolio(
            @RequestAttribute("currentCustomerId") String customerId) {
        List<Account> portfolio = accountsService.getAllAccountsForCustomer(customerId);
        return ResponseEntity.ok(portfolio);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<List<Account>> getAccountDetails(
            @RequestAttribute("currentCustomerId") String customerId,@PathVariable("accountNumber") String accountNumber) {
        List<Account> accounts = accountsService.getAllAccountsForAccount(customerId,accountNumber);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<TransactionDto> getAccountLedger(@PathVariable("accountId") String accountId) {
        List<TransactionDto> transactions = transactionService.getTransactionsByAccountId(accountId);
        return ResponseEntity.ok(transactions);
    }
}