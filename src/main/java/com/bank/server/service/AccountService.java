package com.bank.server.service;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bank.server.dto.AccountDTO;
import com.bank.server.entity.Account;
import com.bank.server.entity.Customer;
import com.bank.server.entity.Product;
import com.bank.server.enums.AccountStatus;
import com.bank.server.exception.AccountNotFoundException;
import com.bank.server.mapper.AccountMapper;
import com.bank.server.repository.AccountRepository;
import com.bank.server.utils.AccountNumberGenerator;
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

    public List<Account> getAllAccountsForCustomer(String customerId) {
        List<Account> accounts = accountRepository.getAccountsWithProductByCustomerId(customerId);
        if (accounts.isEmpty()) {
            throw new AccountNotFoundException("No Accounts found");
        }
        loggerService.log(
                "FETCH_ALL_ACCOUNTS",
                "Fetched all accounts for customer" + customerId,
                LogType.SUCCESS);
        return accounts;
    }

    public List<Account> getAllAccountsForAccount(String customerId, String accountNumber) {
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

        return accounts;
    }

    @Transactional
    public AccountDTO createAccount(AccountDTO accountDTO) {
        Account account=accountMapper.toEntity(accountDTO);
        String accountNumber = AccountNumberGenerator.generate();
        account.setAccountNumber(accountNumber);
        account.setId(UuidGeneratorUtil.generateUuid());

        Account savedAccount=accountRepository.save(account);

        loggerService.log(
        "CREATE_ACCOUNT",
        "Created Account ",
        LogType.SUCCESS);
        return accountMapper.toDto(savedAccount);

    }
}