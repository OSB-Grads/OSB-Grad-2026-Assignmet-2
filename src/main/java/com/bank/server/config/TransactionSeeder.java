package com.bank.server.config;

import com.bank.server.entity.Account;
import com.bank.server.entity.Customer;
import com.bank.server.entity.Transaction;
import com.bank.server.enums.TransactionStatus;
import com.bank.server.repository.AccountRepository;
import com.bank.server.repository.CustomerRepository;
import com.bank.server.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
@Order(5)
@RequiredArgsConstructor
public class TransactionSeeder implements CommandLineRunner {

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    @Override
    public void run(String... args) {

        if (transactionRepository.count() == 0) {

            Customer shivakumar = customerRepository.findByUsername("shivakumar")
                    .orElseThrow();

            Customer charu = customerRepository.findByUsername("charu")
                    .orElseThrow();

            Customer akash = customerRepository.findByUsername("akash")
                    .orElseThrow();

            Account account1 = accountRepository.findByAccountNumber("ACC100001")
                    .orElseThrow();

            Account account2 = accountRepository.findByAccountNumber("ACC100002")
                    .orElseThrow();

            Account account3 = accountRepository.findByAccountNumber("ACC100003")
                    .orElseThrow();

            Transaction transaction1 = new Transaction();
            transaction1.setId(UUID.randomUUID().toString());
            transaction1.setCustomer(shivakumar);
            transaction1.setFromAccount(account1);
            transaction1.setToAccount(account2);
            transaction1.setTransactionType("TRANSFER");
            transaction1.setAmount(new BigDecimal("5000.00"));
            transaction1.setStatus(TransactionStatus.COMPLETED);
            transaction1.setDescription("Transfer to Charu");

            Transaction transaction2 = new Transaction();
            transaction2.setId(UUID.randomUUID().toString());
            transaction2.setCustomer(charu);
            transaction2.setFromAccount(account2);
            transaction2.setToAccount(account3);
            transaction2.setTransactionType("TRANSFER");
            transaction2.setAmount(new BigDecimal("3000.00"));
            transaction2.setStatus(TransactionStatus.COMPLETED);
            transaction2.setDescription("Transfer to Akash");

            Transaction transaction3 = new Transaction();
            transaction3.setId(UUID.randomUUID().toString());
            transaction3.setCustomer(akash);
            transaction3.setFromAccount(account3);
            transaction3.setToAccount(account1);
            transaction3.setTransactionType("TRANSFER");
            transaction3.setAmount(new BigDecimal("2500.00"));
            transaction3.setStatus(TransactionStatus.COMPLETED);
            transaction3.setDescription("Transfer to Shivakumar");

            transactionRepository.saveAll(List.of(
                    transaction1,
                    transaction2,
                    transaction3
            ));
        }
    }
}