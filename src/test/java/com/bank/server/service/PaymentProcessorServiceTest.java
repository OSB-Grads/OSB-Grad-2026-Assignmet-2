package com.bank.server.service;

import com.bank.server.dto.AccountDTO;
import com.bank.server.dto.InboxDTO;
import com.bank.server.dto.TransactionDTO;
import com.bank.server.entity.Account;
import com.bank.server.entity.Customer;
import com.bank.server.enums.LogType;
import com.bank.server.enums.TransactionStatus;
import com.bank.server.exception.TransactionNotFoundException;
import com.bank.server.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentProcessorServiceTest {
    @Mock
    private LoggerService loggerService;

    @Mock
    private InboxService inboxService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private PaymentProcessorService paymentProcessorService;

    @Test
    void processDeposit_ShouldProcessDepositSuccessfully() {

        Map<String, Object> payload = new HashMap<>();
        payload.put("targetAccountId", "ACCOUNT_1");
        payload.put("amount", "500");

        InboxDTO inbox = InboxDTO.builder()
                .id("INBOX_1")
                .payload(payload)
                .build();

        Customer customer = Customer.builder()
                .id("CUSTOMER_1")
                .build();

        Account account = Account.builder()
                .id("ACCOUNT_1")
                .customer(customer)
                .balance(new BigDecimal("1000"))
                .build();

        TransactionDTO savedTransaction = TransactionDTO.builder()
                .id("TRANSACTION_1")
                .build();

        when(accountRepository.findById("ACCOUNT_1"))
                .thenReturn(Optional.of(account));

        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(transactionService.createTransaction(any(TransactionDTO.class)))
                .thenReturn(savedTransaction);

        paymentProcessorService.processDeposit(inbox);
        verify(accountRepository)
                .findById("ACCOUNT_1");

        ArgumentCaptor<Account> accountCaptor =
                ArgumentCaptor.forClass(Account.class);

        verify(accountRepository)
                .save(accountCaptor.capture());

        Account savedAccount = accountCaptor.getValue();

        assertEquals(
                new BigDecimal("1500"),
                savedAccount.getBalance()
        );

        ArgumentCaptor<TransactionDTO> transactionCaptor =
                ArgumentCaptor.forClass(TransactionDTO.class);

        verify(transactionService)
                .createTransaction(transactionCaptor.capture());

        TransactionDTO transaction =
                transactionCaptor.getValue();

        assertEquals("CUSTOMER_1", transaction.getCustomerId());
        assertEquals("ACCOUNT_1", transaction.getToAccountId());
        assertEquals(new BigDecimal("500"), transaction.getAmount());
        assertEquals(TransactionStatus.COMPLETED, transaction.getStatus());
        assertEquals("DEPOSIT", transaction.getTransactionType());
        assertEquals("Deposit Successful", transaction.getDescription());

        verify(loggerService).log(
                eq("PROCESS_DEPOSIT"),
                contains("Processed deposit"),
                eq(LogType.SUCCESS)
        );

        verify(inboxService)
                .deleteById("INBOX_1");
    }

    @Test
    void processDeposit_ShouldHandleAccountNotFound() {

        Map<String, Object> payload = new HashMap<>();
        payload.put("targetAccountId", "ACCOUNT_1");
        payload.put("amount", "500");

        InboxDTO inbox = InboxDTO.builder()
                .id("INBOX_1")
                .payload(payload)
                .build();

        when(accountRepository.findById("ACCOUNT_1"))
                .thenReturn(Optional.empty());

        paymentProcessorService.processDeposit(inbox);

        verify(accountRepository)
                .findById("ACCOUNT_1");

        verify(accountRepository, never())
                .save(any(Account.class));

        verify(transactionService, never())
                .createTransaction(any(TransactionDTO.class));

        verify(loggerService)
                .log(
                        eq("PROCESS_DEPOSIT"),
                        contains("Account not found"),
                        eq(LogType.FAILURE)
                );

        verify(inboxService)
                .deleteById("INBOX_1");
    }

    @Test
    void processWithdrawalResponse_ShouldCompleteWithdrawalSuccessfully() {

        Map<String, Object> payload = new HashMap<>();
        payload.put("success", true);

        InboxDTO inbox = InboxDTO.builder()
                .id("INBOX_1")
                .transactionId("TXN_1")
                .payload(payload)
                .build();

        TransactionDTO transaction = TransactionDTO.builder()
                .id("TXN_1")
                .fromAccountId("ACCOUNT_1")
                .amount(new BigDecimal("500"))
                .build();

        when(transactionService.getTransactionById("TXN_1"))
                .thenReturn(transaction);

        paymentProcessorService.processWithdrawalResponse(inbox);
        verify(transactionService)
                .getTransactionById("TXN_1");

        verify(transactionService)
                .updateTransaction(
                        "TXN_1",
                        TransactionStatus.COMPLETED
                );

        verify(accountService, never())
                .creditAmount(anyString(), any(BigDecimal.class));

        verify(loggerService)
                .log(
                        eq("PROCESS_WITHDRAWAL"),
                        contains("Processed withdrawal"),
                        eq(LogType.SUCCESS)
                );

        verify(inboxService)
                .deleteById("INBOX_1");
    }

    @Test
    void processWithdrawalResponse_ShouldRefundAmount_WhenWithdrawalFails() {

        Map<String, Object> payload = new HashMap<>();
        payload.put("success", false);

        InboxDTO inbox = InboxDTO.builder()
                .id("INBOX_1")
                .transactionId("TXN_1")
                .payload(payload)
                .build();

        TransactionDTO transaction = TransactionDTO.builder()
                .id("TXN_1")
                .fromAccountId("ACCOUNT_1")
                .amount(new BigDecimal("500"))
                .build();

        AccountDTO accountDTO = AccountDTO.builder()
                .id("ACCOUNT_1")
                .build();

        when(transactionService.getTransactionById("TXN_1"))
                .thenReturn(transaction);

        when(accountService.creditAmount(
                "ACCOUNT_1",
                new BigDecimal("500")))
                .thenReturn(accountDTO);

        paymentProcessorService.processWithdrawalResponse(inbox);

        verify(transactionService)
                .getTransactionById("TXN_1");

        verify(accountService)
                .creditAmount(
                        "ACCOUNT_1",
                        new BigDecimal("500")
                );

        verify(transactionService)
                .updateTransaction(
                        "TXN_1",
                        TransactionStatus.FAILED
                );

        verify(transactionService, never())
                .updateTransaction(
                        "TXN_1",
                        TransactionStatus.COMPLETED
                );

        verify(loggerService)
                .log(
                        eq("PROCESS_WITHDRAWAL"),
                        contains("Withdrawal failed"),
                        eq(LogType.FAILURE)
                );

        verify(inboxService)
                .deleteById("INBOX_1");
    }

    @Test
    void processWithdrawalResponse_ShouldThrowException_WhenTransactionNotFound() {

        Map<String, Object> payload = new HashMap<>();
        payload.put("success", true);

        InboxDTO inbox = InboxDTO.builder()
                .id("INBOX_1")
                .transactionId("TXN_1")
                .payload(payload)
                .build();

        when(transactionService.getTransactionById("TXN_1"))
                .thenThrow(
                        new TransactionNotFoundException(
                                "TRANSACTION_NOT_FOUND",
                                "Transaction not found"
                        )
                );

        assertThrows(
                TransactionNotFoundException.class,
                () -> paymentProcessorService.processWithdrawalResponse(inbox)
        );

        verify(transactionService)
                .getTransactionById("TXN_1");

        verify(accountService, never())
                .creditAmount(anyString(), any(BigDecimal.class));

        verify(transactionService, never())
                .updateTransaction(anyString(), any(TransactionStatus.class));

        verify(loggerService, never())
                .log(anyString(), anyString(), any(LogType.class));

        verify(inboxService, never())
                .deleteById(anyString());
    }

    @Test
    void processWithdrawalResponse_ShouldThrowException_WhenRefundFails() {

        Map<String, Object> payload = new HashMap<>();
        payload.put("success", false);

        InboxDTO inbox = InboxDTO.builder()
                .id("INBOX_1")
                .transactionId("TXN_1")
                .payload(payload)
                .build();

        TransactionDTO transaction = TransactionDTO.builder()
                .id("TXN_1")
                .fromAccountId("ACCOUNT_1")
                .amount(new BigDecimal("500"))
                .build();

        when(transactionService.getTransactionById("TXN_1"))
                .thenReturn(transaction);

        when(accountService.creditAmount(
                "ACCOUNT_1",
                new BigDecimal("500")))
                .thenThrow(new RuntimeException("Refund failed"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> paymentProcessorService.processWithdrawalResponse(inbox)
        );

        assertEquals("Refund failed", exception.getMessage());

        verify(transactionService)
                .getTransactionById("TXN_1");

        verify(accountService)
                .creditAmount(
                        "ACCOUNT_1",
                        new BigDecimal("500")
                );

        verify(transactionService, never())
                .updateTransaction(anyString(), any(TransactionStatus.class));

        verify(loggerService, never())
                .log(anyString(), anyString(), any(LogType.class));

        verify(inboxService, never())
                .deleteById(anyString());
    }
    @Test
    void processWithdrawalResponse_ShouldTreatNullSuccessAsFailure() {

        Map<String, Object> payload = new HashMap<>();

        InboxDTO inbox = InboxDTO.builder()
                .id("INBOX_1")
                .transactionId("TXN_1")
                .payload(payload)
                .build();

        TransactionDTO transaction = TransactionDTO.builder()
                .id("TXN_1")
                .fromAccountId("ACCOUNT_1")
                .amount(new BigDecimal("500"))
                .build();

        AccountDTO accountDTO = AccountDTO.builder()
                .id("ACCOUNT_1")
                .build();

        when(transactionService.getTransactionById("TXN_1"))
                .thenReturn(transaction);

        when(accountService.creditAmount(
                "ACCOUNT_1",
                new BigDecimal("500")))
                .thenReturn(accountDTO);

        paymentProcessorService.processWithdrawalResponse(inbox);

        verify(transactionService)
                .getTransactionById("TXN_1");

        verify(accountService)
                .creditAmount(
                        "ACCOUNT_1",
                        new BigDecimal("500")
                );

        verify(transactionService)
                .updateTransaction(
                        "TXN_1",
                        TransactionStatus.FAILED
                );

        verify(transactionService, never())
                .updateTransaction(
                        "TXN_1",
                        TransactionStatus.COMPLETED
                );

        verify(loggerService)
                .log(
                        eq("PROCESS_WITHDRAWAL"),
                        contains("Withdrawal failed"),
                        eq(LogType.FAILURE)
                );

        verify(inboxService)
                .deleteById("INBOX_1");
    }
}
