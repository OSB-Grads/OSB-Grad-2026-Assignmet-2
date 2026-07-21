package com.bank.server.service;

import lombok.extern.slf4j.Slf4j;
import com.bank.server.enums.LogType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bank.server.dto.AccountDTO;
import com.bank.server.dto.ViewAccountResponseDTO;
import com.bank.server.entity.Account;
import com.bank.server.entity.Customer;
import com.bank.server.entity.Product;
import com.bank.server.enums.AccountStatus;
import com.bank.server.enums.LogType;
import com.bank.server.exception.AccountNotFoundException;
import com.bank.server.mapper.AccountMapper;
import com.bank.server.repository.AccountRepository;
import com.bank.server.utils.Generator;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final LoggerService loggerService;

    @PreAuthorize("hasRole('CUSTOMER')")
    public List<ViewAccountResponseDTO> getAllAccountsForCustomer(String customerId) {
        List<Account> accounts = accountRepository.getAccountsWithProductByCustomerId(customerId);
        if (accounts.isEmpty()) {
            throw new AccountNotFoundException("ACCOUNTS_NOT_FOUND","No Accounts found");
        }
        loggerService.log(
                "FETCH_ALL_ACCOUNTS",
                "Fetched all accounts for customer" + customerId,
                LogType.SUCCESS);
        return accounts.stream().map(accountMapper::toViewDto).toList();
    }

    @PreAuthorize("hasRole('CUSTOMER')")
    public ViewAccountResponseDTO getAccountForAccountId(String customerId, String accountNumber) {
        Account account = accountRepository.getAccountsWithProductByCustomerId(customerId)
                .stream()
                .filter(acc -> acc.getAccountNumber().equals(accountNumber))
                .findFirst().orElse(null);
        if (account == null) {
            throw new AccountNotFoundException("ACCOUNT_NOT_FOUND","No Account found");
        }

        loggerService.log(
                "FETCH_ACCOUNT",
                "Fetched account " + accountNumber,
                LogType.SUCCESS);

        ViewAccountResponseDTO accountDtos = accountMapper.toViewDto(account);
        return accountDtos;
    }

    @Transactional
    @PreAuthorize("hasRole('CUSTOMER')")
    public AccountDTO createAccount(AccountDTO accountDTO) {
        Account account = accountMapper.toEntity(accountDTO);
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountStatus.ACTIVE);
        account.setLocked(false);
        account.setAccountNumber(Generator.generateAccountNumber());
        account.setId(Generator.generateUuid());
        Account savedAccount = accountRepository.save(account);

        loggerService.log(
                "CREATE_ACCOUNT",
                "Created Account ",
                LogType.SUCCESS);
        return accountMapper.toDto(savedAccount);

    }

    public AccountDTO reserveAmount(String accountId, BigDecimal amount) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        "ACCOUNT_NOT_FOUND",
                        "Account not found for id " + accountId));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    "INSUFFICIENT_BALANCE",
                    "Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(amount));

        Account savedAccount = accountRepository.save(account);

        return accountMapper.toDto(savedAccount);
    }

    public AccountDTO creditAmount(String accountId, BigDecimal amount)
    {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        "ACCOUNT_NOT_FOUND",
                        "Account not found for id " + accountId));

        account.setBalance(account.getBalance().add(amount));

        Account savedAccount = accountRepository.save(account);

        return accountMapper.toDto(savedAccount);
    public Account getAccountForUpdate(String accountNumber) {
        return accountRepository
                .findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(
                        "ACCOUNT_NOT_FOUND",
                        "Account not found with account number: " + accountNumber)
                );
    }
    public void transferAmount(
            Account sourceAccount,
            Account destinationAccount,
            BigDecimal amount)
    {
        BigDecimal sourceBalanceAfter = sourceAccount.getBalance().subtract(amount);

        BigDecimal destinationBalanceAfter = destinationAccount.getBalance().add(amount);

        sourceAccount.setBalance(sourceBalanceAfter);
        destinationAccount.setBalance(destinationBalanceAfter);

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);
    }
}