package com.bank.server.service;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bank.server.entity.Account;
import com.bank.server.entity.Customer;
import com.bank.server.entity.Product;
import com.bank.server.enums.AccountStatus;
import com.bank.server.repository.AccountRepository;
import com.bank.server.utils.AccountNumberGenerator;

import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class AccountsService {

    private final AccountRepository accountRepository;
    private final EntityManager entityManager;

    public AccountsService(AccountRepository accountRepository,
            AccountNumberGenerator accountNumberGenerator,
            EntityManager entityManager) {
        this.accountRepository = accountRepository;
        this.entityManager = entityManager;
    }

    public List<Map<String, Object>> getAllAccountsForCustomer(String customerId) {
        try {
            List<Map<String, Object>> rows = accountRepository.getAccountsWithProductByCustomerId(customerId);

            if (rows == null || rows.isEmpty()) {
                // loggerService.log(
                // "FETCH_ALL_ACCOUNTS",
                // "No Accounts found for customer: " + customerId,
                // LogType.ERROR);
                return Collections.emptyList();
            }

            // loggerService.log(
            // "FETCH_ALL_ACCOUNTS",
            // "Fetched all accounts for customer: " + customerId,
            // LogType.SUCCESS);
            return rows;

        } catch (DataAccessException e) { // Catches all Spring database runtime exceptions
            // loggerService.log(
            // "FETCH_ALL_ACCOUNTS",
            // "Failed to fetch all accounts for customer: " + customerId + ". Reason: " +
            // e.getMessage(),
            // LogType.FAILURE);
            throw e;
        } catch (Exception e) {
            // loggerService.log(
            // "FETCH_ALL_ACCOUNTS",
            // "Failed to fetch all accounts for customer: " + customerId + ". Reason: " +
            // e.getMessage(),
            // LogType.FAILURE);
            throw e;
        }
    }

    @Transactional
    public String createAccount(String customerId, String productId) {
        try {
            String accountNumber = AccountNumberGenerator.generate();
            Account account = new Account();
            account.setId(UUID.randomUUID().toString());
            account.setAccountNumber(accountNumber);
            Customer customerRef = entityManager.getReference(Customer.class, customerId);
            Product productRef = entityManager.getReference(Product.class, productId);
            account.setCustomer(customerRef);
            account.setProduct(productRef);
            account.setBalance(BigDecimal.ZERO);
            account.setStatus(AccountStatus.ACTIVE);
            account.setLocked(false);
            accountRepository.save(account);

            // loggerService.log(
            // "CREATE_ACCOUNT",
            // "Created Account for the product " + productId,
            // LogType.SUCCESS);
            return accountNumber;

        } catch (DataAccessException e) {
            // loggerService.log(
            // "CREATE_ACCOUNT",
            // "Failed to Create Account. Reason: " + e.getMessage(),
            // LogType.FAILURE);
            throw e;
        } catch (Exception e) {
            // loggerService.log(
            // "CREATE_ACCOUNT",
            // "Failed to do Create Account Operation. Reason: " + e.getMessage(),
            // LogType.FAILURE);
            throw e;
        }
    }
}