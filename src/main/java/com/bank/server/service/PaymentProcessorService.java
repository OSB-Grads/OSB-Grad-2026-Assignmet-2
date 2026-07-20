package com.bank.server.service;

import com.bank.server.dto.AccountDTO;
import com.bank.server.dto.InboxDTO;
import com.bank.server.dto.TransactionDTO;
import com.bank.server.entity.Account;
import com.bank.server.enums.LogType;
import com.bank.server.enums.TransactionStatus;
import com.bank.server.exception.AccountNotFoundException;
import com.bank.server.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProcessorService {

    private final LoggerService loggerService;
    private final InboxService inboxService;
    private final TransactionService transactionService;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    @Transactional
    public void processDeposit(InboxDTO inbox) {
        String targetAccountId = inbox.getPayload().get("targetAccountId").toString();
        log.info("Processing deposit for account {}", targetAccountId);
        try {
            BigDecimal amount = new BigDecimal(inbox.getPayload().get("amount").toString());

            Account account = accountRepository.findById(targetAccountId)
                    .orElseThrow(() -> new AccountNotFoundException("ACCOUNT_NOT_FOUND", "Account not found for account id " + targetAccountId));

            account.setBalance(account.getBalance().add(amount));

            Account updatedAccount = accountRepository.save(account);

            TransactionDTO transactionDTO = TransactionDTO.builder()
                    .customerId(updatedAccount.getCustomer().getId())
                    .toAccountId(updatedAccount.getId())
                    .transactionType("DEPOSIT")
                    .amount(amount)
                    .status(TransactionStatus.COMPLETED)
                    .description("Deposit Successful")
                    .build();

            TransactionDTO savedTransaction = transactionService.createTransaction(transactionDTO);

            log.info("Created deposit transaction {}", savedTransaction.getId());

            log.info("Deposit of {} processed successfully for account {}", amount, updatedAccount.getId());

            loggerService.log(
                    "PROCESS_DEPOSIT",
                    "Processed deposit of " + amount +
                            " to account " + updatedAccount.getId(),
                    LogType.SUCCESS);

            inboxService.deleteById(inbox.getId());

        } catch (AccountNotFoundException e) {
            log.error("Deposit processing failed:", e);

            loggerService.log(
                    "PROCESS_DEPOSIT",
                    e.getMessage(),
                    LogType.FAILURE);

            inboxService.deleteById(inbox.getId());
        }
    }

    @Transactional
    public void processWithdrawal(InboxDTO inbox) {
        log.info("Processing withdrawal for account {}", inbox.getPayload().get("targetAccountId"));

        String transactionId = inbox.getTransactionId();
        String accountId = inbox.getPayload().get("targetAccountId").toString();
        BigDecimal amount = new BigDecimal(inbox.getPayload().get("amount").toString());

        boolean success = realWorldBankService.withdraw(inbox);

        if(success)
        {
            transactionService.updateTransaction(transactionId, TransactionStatus.COMPLETED);
            log.info("Withdrawal of {} processed successfully for account {}",amount,accountId);

            loggerService.log(
                    "PROCESS_WITHDRAWAL",
                    "Processed withdrawal of " + amount +
                            " from account " + accountId,
                    LogType.SUCCESS);

            inboxService.deleteById(inbox.getId());
        }else{
            AccountDTO accountDTO = accountService.creditAmount(accountId,amount);

            transactionService.updateTransaction(transactionId,TransactionStatus.FAILED);
             loggerService.log(
                     "PROCESS_WITHDRAWAL",
                     "Withdrawal failed for account " + accountId +
                             ". Amount refunded: " + amount,
                     LogType.FAILURE
             );

             inboxService.deleteById(inbox.getId());
        }
    }
}
