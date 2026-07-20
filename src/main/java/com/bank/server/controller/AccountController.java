package com.bank.server.controller;

import lombok.Builder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.bank.server.dto.AccountDTO;
import com.bank.server.dto.AccountTransactionDto;
import com.bank.server.dto.TransactionDTO;
import com.bank.server.dto.ViewAccountResponseDTO;
import com.bank.server.security.CustomUserDetails;
import com.bank.server.service.AccountService;
import com.bank.server.service.TransactionService;
import jakarta.validation.Valid;
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
    public ResponseEntity<AccountDTO> createAccount(Authentication authentication ,@Valid @RequestBody AccountDTO accountDto) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        String customerId = user.getCustomerId();
        accountDto.setCustomerId(customerId);
        AccountDTO account = accountsService.createAccount(accountDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    @GetMapping
    public ResponseEntity<List<ViewAccountResponseDTO>> getCustomerPortfolio(
        Authentication authentication) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        String customerId = user.getCustomerId();
        List<ViewAccountResponseDTO> portfolio = accountsService.getAllAccountsForCustomer(customerId);
        return ResponseEntity.ok(portfolio);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountTransactionDto> getAccountDetails(
            Authentication authentication,
            @PathVariable("accountNumber") String accountNumber) {
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        String customerId = user.getCustomerId();
        ViewAccountResponseDTO account = accountsService.getAccountForAccountId(customerId, accountNumber);
        List<TransactionDTO> transactions = transactionService.getTransactionsByAccountNumber(accountNumber);
        AccountTransactionDto accountTransactionDto = new AccountTransactionDto(account, transactions);
        return ResponseEntity.ok(accountTransactionDto);
    }
}