package com.bank.server.service;

import com.bank.server.enums.LogType;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bank.server.dto.AccountDTO;
import com.bank.server.dto.ViewAccountResponseDTO;
import com.bank.server.entity.Account;
import com.bank.server.entity.Customer;
import com.bank.server.entity.Product;
import com.bank.server.enums.AccountStatus;
import com.bank.server.exception.AccountNotFoundException;
import com.bank.server.mapper.AccountMapper;
import com.bank.server.repository.AccountRepository;
import com.bank.server.utils.Generator;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final AccountMapper accountMapper;

    public List<ViewAccountResponseDTO> getAllAccountsForCustomer(String customerId) {
        List<Account> accounts = accountRepository.getAccountsWithProductByCustomerId(customerId);
        if (accounts.isEmpty()) {
            throw new AccountNotFoundException("No Accounts found");
        }
        loggerService.log(
                "FETCH_ALL_ACCOUNTS",
                "Fetched all accounts for customer" + customerId,
                LogType.SUCCESS);
        return accounts.stream()
            .map(accountMapper::toViewDto)
            .toList();
    }

    public List<ViewAccountResponseDTO> getAllAccountsForAccount(String customerId, String accountNumber) {
        List<Account> accounts = accountRepository.getAccountsWithProductByCustomerId(customerId);
        accounts
                .stream()
                .filter(account -> account.getAccountNumber().equals(accountNumber))
                .toList();
        if (accounts.isEmpty()) {
            throw new AccountNotFoundException("No Accounts found");
        }
        loggerService.log(
                "FETCH_ALL_ACCOUNTS",
                "Fetched all accounts for customer" + customerId,
                LogType.SUCCESS);

         return accounts.stream()
            .map(accountMapper::toViewDto)
            .toList();
    }

    @Transactional
    public AccountDTO createAccount(AccountDTO accountDTO) {
        Account account=accountMapper.toEntity(accountDTO);
        String accountNumber = Generator.generateAccountNumber();
        account.setAccountNumber(accountNumber);
        account.setId(UUID.randomUUID().toString());

        Account savedAccount=accountRepository.save(account);

        loggerService.log(
        "CREATE_ACCOUNT",
        "Created Account ",
        LogType.SUCCESS);
        return accountMapper.toDto(savedAccount);

    }
    public Account getAccountForUpdate(String accountNumber) {
        return accountRepository
                .findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(
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