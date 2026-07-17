package com.bank.server.service;

import com.bank.server.dto.PaymentQueueDTO;
import com.bank.server.dto.TransactionDTO;
import com.bank.server.entity.Account;
import com.bank.server.enums.LogType;
import com.bank.server.exception.AccountNotFoundException;
import com.bank.server.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.bank.server.exception.InsufficientBalanceException;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProcessorService {

    private final LoggerService loggerService;
    private final PaymentQueueService paymentQueueService;
    private final TransactionService transactionService;
    private final AccountRepository accountRepository;

    @Transactional
    public void processDeposit(PaymentQueueDTO entry)
    {
        log.info("Processing deposit for account {}", entry.getTargetAccountId());
        try{
            Account account = accountRepository.findById(entry.getTargetAccountId())
                    .orElseThrow(()-> new AccountNotFoundException("ACCOUNT_NOT_FOUND","Account not found for account id "+entry.getTargetAccountId()));

            account.setBalance(account.getBalance().add(entry.getAmount()));

            Account updatedAccount = accountRepository.save(account);

            TransactionDTO transactionDTO = TransactionDTO.builder()
                    .customerId(updatedAccount.getCustomer().getId())
                    .toAccountId(updatedAccount.getId())
                    .transactionType("DEPOSIT")
                    .amount(entry.getAmount())
                    .status("COMPLETED")
                    .description("Deposit Successful")
                    .build();

            TransactionDTO savedTransaction = transactionService.createTransaction(transactionDTO);

            log.info("Created deposit transaction {}",savedTransaction.getId());

            log.info("Deposit of {} processed successfully for account {}",entry.getAmount(), updatedAccount.getId());

            loggerService.log(
                    "PROCESS_DEPOSIT",
                    "Processed deposit of " + entry.getAmount() +
                            " to account " + updatedAccount.getAccountNumber(),
                    LogType.SUCCESS);

            paymentQueueService.deleteById(entry.getId());

        }catch(AccountNotFoundException e){
            log.error("Deposit processing failed:", e);

            loggerService.log(
                    "PROCESS_DEPOSIT",
                    e.getMessage(),
                    LogType.FAILURE);

            paymentQueueService.deleteById(entry.getId());
        }
    }

    @Transactional
    public void processWithdrawal(PaymentQueueDTO entry)
    {
        log.info("Processing withdrawal for account {}", entry.getTargetAccountId());
        try{
            Account account = accountRepository.findById(entry.getTargetAccountId())
                    .orElseThrow(()-> new AccountNotFoundException("ACCOUNT_NOT_FOUND","Account not found for account id "+entry.getTargetAccountId()));

            if (account.getBalance().compareTo(entry.getAmount()) < 0) {
                throw new InsufficientBalanceException(
                        "INSUFFICIENT_BALANCE",
                        "Insufficient balance for withdrawal"
                );
            }

            account.setBalance(account.getBalance().subtract(entry.getAmount()));

            Account updatedAccount = accountRepository.save(account);

            TransactionDTO transactionDTO = TransactionDTO.builder()
                    .customerId(updatedAccount.getCustomer().getId())
                    .fromAccountId(updatedAccount.getId())
                    .transactionType("WITHDRAWAL")
                    .amount(entry.getAmount())
                    .status("COMPLETED")
                    .description("Withdrawal Successful")
                    .build();

            TransactionDTO savedTransaction = transactionService.createTransaction(transactionDTO);

            log.info("Created withdrawal transaction {}",savedTransaction.getId());

            log.info("Withdrawal of {} processed successfully for account {}",entry.getAmount(), updatedAccount.getId());

            loggerService.log(
                    "PROCESS_WITHDRAWAL",
                    "Processed withdrawal of " + entry.getAmount() +
                            " from account " + updatedAccount.getAccountNumber(),
                    LogType.SUCCESS);

            paymentQueueService.deleteById(entry.getId());

        }catch(AccountNotFoundException | InsufficientBalanceException e){
            log.error("Withdrawal processing failed: ", e);

            loggerService.log(
                    "PROCESS_WITHDRAWAL",
                    e.getMessage(),
                    LogType.FAILURE);

            paymentQueueService.deleteById(entry.getId());
        }
    }
}
