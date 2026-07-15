package com.bank.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.bank.server.dto.AccountDTO;
import com.bank.server.dto.AccountTransactionDto;
import com.bank.server.dto.TransactionDTO;
import com.bank.server.dto.ViewAccountResponseDTO;
import com.bank.server.service.AccountService;
import com.bank.server.service.TransactionService;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RequiredArgsConstructor
@Builder
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountsService;
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@Valid @RequestBody AccountDTO accountDto) {
        AccountDTO account = accountsService.createAccount(accountDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    @GetMapping
    public ResponseEntity<List<ViewAccountResponseDTO>> getCustomerPortfolio(
            @RequestAttribute("currentCustomerId") String customerId) {
        List<ViewAccountResponseDTO> portfolio = accountsService.getAllAccountsForCustomer(customerId);
        return ResponseEntity.ok(portfolio);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountTransactionDto> getAccountDetails(
            @RequestAttribute("currentCustomerId") String customerId,
            @PathVariable("accountNumber") String accountId) {
        ViewAccountResponseDTO account = accountsService.getAccountForAccountId(customerId, accountId);
        List<TransactionDTO> transactions = transactionService.getTransactionsByAccountId(accountId);
        AccountTransactionDto accountTransactionDto = new AccountTransactionDto(account, transactions);
        return ResponseEntity.ok(accountTransactionDto);
    }
}