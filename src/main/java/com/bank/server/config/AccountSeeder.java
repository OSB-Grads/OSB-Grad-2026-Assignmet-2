package com.bank.server.config;

import com.bank.server.entity.Account;
import com.bank.server.entity.Auth;
import com.bank.server.entity.Customer;
import com.bank.server.entity.Product;
import com.bank.server.enums.AccountStatus;
import com.bank.server.repository.AccountRepository;
import com.bank.server.repository.AuthRepository;
import com.bank.server.repository.CustomerRepository;
import com.bank.server.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
@Order(4)
@RequiredArgsConstructor
public class AccountSeeder implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final AuthRepository  authRepository;

    @Override
    public void run(String... args) {

        if (accountRepository.count() == 0) {

            Auth shivakumarAuth = authRepository.findByUsername("shivakumar")
                    .orElseThrow();

            Customer shivakumar = customerRepository.findById(shivakumarAuth.getId())
                    .orElseThrow();

            Auth charuAuth = authRepository.findByUsername("charu")
                    .orElseThrow();

            Customer charu = customerRepository.findById(charuAuth.getId())
                    .orElseThrow();

            Auth akashAuth = authRepository.findByUsername("akash")
                    .orElseThrow();

            Customer akash = customerRepository.findById(akashAuth.getId())
                    .orElseThrow();

            Product savingsBasic = productRepository.findByProductName("Savings Basic")
                    .orElseThrow();

            Product savingsPremium = productRepository.findByProductName("Savings Premium")
                    .orElseThrow();

            Product fixedDeposit12 = productRepository.findByProductName("Fixed Deposit 12 Months")
                    .orElseThrow();

            Account account1 = new Account();
            account1.setId(UUID.randomUUID().toString());
            account1.setAccountNumber("ACC100001");
            account1.setCustomer(shivakumar);
            account1.setProduct(savingsBasic);
            account1.setBalance(new BigDecimal("25000.00"));
            account1.setLocked(false);
            account1.setStatus(AccountStatus.ACTIVE);

            Account account2 = new Account();
            account2.setId(UUID.randomUUID().toString());
            account2.setAccountNumber("ACC100002");
            account2.setCustomer(charu);
            account2.setProduct(savingsPremium);
            account2.setBalance(new BigDecimal("50000.00"));
            account2.setLocked(false);
            account2.setStatus(AccountStatus.ACTIVE);

            Account account3 = new Account();
            account3.setId(UUID.randomUUID().toString());
            account3.setAccountNumber("ACC100003");
            account3.setCustomer(akash);
            account3.setProduct(fixedDeposit12);
            account3.setBalance(new BigDecimal("100000.00"));
            account3.setLocked(false);
            account3.setStatus(AccountStatus.ACTIVE);

            accountRepository.saveAll(List.of(
                    account1,
                    account2,
                    account3
            ));
        }
    }
}