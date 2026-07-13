package com.bank.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.bank.server.dto.AccountDTO;
import com.bank.server.dto.AccountTransactionDto;
import com.bank.server.dto.ViewAccountResponseDTO;
import com.bank.server.entity.Account;
import com.bank.server.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Builder
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountsService;

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@Valid @RequestBody AccountDTO accountDto) {
        AccountDTO accountDto = accountsService.createAccount(accountDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(accountDto);
    }

    @GetMapping 
    public ResponseEntity<List<ViewAccountResponseDTO>> getCustomerPortfolio(
            @RequestAttribute("currentCustomerId") String customerId) {
        List<ViewAccountResponseDTO> portfolio = accountsService.getAllAccountsForCustomer(customerId);
        return ResponseEntity.ok(portfolio);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<accountTransactionDto> getAccountDetails(
            @RequestAttribute("currentCustomerId") String customerId,@PathVariable("accountNumber") String accountNumber) {
        List<ViewAccountResponseDTO> account = accountsService.getAllAccountsForAccount(customerId,accountNumber);
        List<TransactionDto> transactions = transactionService.getTransactionsByAccountId(accountId);
        AccountTransactionDto accountTransactionDto = AccountTransactionDto.builder().account(account).transactions(transactions).build();
        return ResponseEntity.ok(accountTransactionDto);
    }
}