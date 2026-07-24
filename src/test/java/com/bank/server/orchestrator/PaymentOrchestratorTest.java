package com.bank.server.orchestrator;

import com.bank.server.dto.AccountDTO;
import com.bank.server.dto.CustomerDTO;
import com.bank.server.dto.TransactionDTO;
import com.bank.server.dto.request.WithdrawRequestDTO;
import com.bank.server.dto.response.InboxResponseDTO;
import com.bank.server.enums.TransactionStatus;
import com.bank.server.service.AccountService;
import com.bank.server.service.CustomerService;
import com.bank.server.service.InboxService;
import com.bank.server.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentOrchestratorTest {
    @Mock
    private TransactionService transactionService;

    @Mock
    private AccountService accountService;

    @Mock
    private InboxService inboxService;

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private PaymentOrchestrator paymentOrchestrator;

    @Test
    void processWithdrawalRequest_ShouldProcessSuccessfully() {

        // Arrange
        WithdrawRequestDTO request = WithdrawRequestDTO.builder()
                .targetAccountId("ACCOUNT_1")
                .amount(new BigDecimal("500"))
                .build();

        AccountDTO account = AccountDTO.builder()
                .customerId("CUSTOMER_1")
                .build();

        CustomerDTO customer = CustomerDTO.builder()
                .nationalId("NATIONAL_123")
                .build();

        TransactionDTO savedTransaction = TransactionDTO.builder()
                .id("TRANSACTION_1")
                .build();

        InboxResponseDTO inboxResponse = InboxResponseDTO.builder()
                .id("INBOX_1")
                .message("Withdrawal queued successfully")
                .build();

        when(accountService.reserveAmount(
                request.getTargetAccountId(),
                request.getAmount()))
                .thenReturn(account);

        when(customerService.getCustomerById(account.getCustomerId()))
                .thenReturn(customer);

        when(transactionService.createTransaction(any(TransactionDTO.class)))
                .thenReturn(savedTransaction);

        when(inboxService.enqueueWithdrawal(
                request,
                savedTransaction.getId(),
                customer.getNationalId()))
                .thenReturn(inboxResponse);

        // Act
        InboxResponseDTO result =
                paymentOrchestrator.processWithdrawalRequest(request);

        // Assert
        assertNotNull(result);
        assertEquals("INBOX_1", result.getId());
        assertEquals("Withdrawal queued successfully", result.getMessage());

        verify(accountService)
                .reserveAmount(
                        request.getTargetAccountId(),
                        request.getAmount());

        verify(customerService)
                .getCustomerById(account.getCustomerId());

        ArgumentCaptor<TransactionDTO> captor =
                ArgumentCaptor.forClass(TransactionDTO.class);

        verify(transactionService)
                .createTransaction(captor.capture());

        TransactionDTO transactionSent = captor.getValue();

        assertEquals("CUSTOMER_1", transactionSent.getCustomerId());
        assertEquals("ACCOUNT_1", transactionSent.getFromAccountId());
        assertEquals(new BigDecimal("500"), transactionSent.getAmount());
        assertEquals(TransactionStatus.PENDING, transactionSent.getStatus());
        assertEquals("WITHDRAWAL", transactionSent.getTransactionType());
        assertEquals("Withdrawal Initiated", transactionSent.getDescription());

        verify(inboxService)
                .enqueueWithdrawal(
                        request,
                        savedTransaction.getId(),
                        customer.getNationalId());
    }

    //Reserve amount fails
    @Test
    void processWithdrawalRequest_ShouldThrowException_WhenReserveAmountFails() {

        // Arrange
        WithdrawRequestDTO request = WithdrawRequestDTO.builder()
                .targetAccountId("ACCOUNT_1")
                .amount(new BigDecimal("500"))
                .build();

        when(accountService.reserveAmount(
                request.getTargetAccountId(),
                request.getAmount()))
                .thenThrow(new RuntimeException("Insufficient Balance"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> paymentOrchestrator.processWithdrawalRequest(request)
        );

        assertEquals("Insufficient Balance", exception.getMessage());

        verify(accountService)
                .reserveAmount(request.getTargetAccountId(), request.getAmount());

        verifyNoInteractions(customerService);
        verifyNoInteractions(transactionService);
        verifyNoInteractions(inboxService);
    }

    //Customer not found exception
    @Test
    void processWithdrawalRequest_ShouldThrowException_WhenCustomerNotFound() {

        // Arrange
        WithdrawRequestDTO request = WithdrawRequestDTO.builder()
                .targetAccountId("ACCOUNT_1")
                .amount(new BigDecimal("500"))
                .build();

        AccountDTO account = AccountDTO.builder()
                .customerId("CUSTOMER_1")
                .build();

        when(accountService.reserveAmount(
                request.getTargetAccountId(),
                request.getAmount()))
                .thenReturn(account);

        when(customerService.getCustomerById(account.getCustomerId()))
                .thenThrow(new RuntimeException("Customer Not Found"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> paymentOrchestrator.processWithdrawalRequest(request)
        );

        assertEquals("Customer Not Found", exception.getMessage());

        verify(accountService)
                .reserveAmount(request.getTargetAccountId(), request.getAmount());

        verify(customerService)
                .getCustomerById(account.getCustomerId());

        verifyNoInteractions(transactionService);
        verifyNoInteractions(inboxService);
    }

    //Create Transaction failed
    @Test
    void processWithdrawalRequest_ShouldThrowException_WhenTransactionCreationFails() {

        // Arrange
        WithdrawRequestDTO request = WithdrawRequestDTO.builder()
                .targetAccountId("ACCOUNT_1")
                .amount(new BigDecimal("500"))
                .build();

        AccountDTO account = AccountDTO.builder()
                .customerId("CUSTOMER_1")
                .build();

        CustomerDTO customer = CustomerDTO.builder()
                .nationalId("NATIONAL_123")
                .build();

        when(accountService.reserveAmount(
                request.getTargetAccountId(),
                request.getAmount()))
                .thenReturn(account);

        when(customerService.getCustomerById(account.getCustomerId()))
                .thenReturn(customer);

        when(transactionService.createTransaction(any(TransactionDTO.class)))
                .thenThrow(new RuntimeException("Transaction Failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> paymentOrchestrator.processWithdrawalRequest(request)
        );

        assertEquals("Transaction Failed", exception.getMessage());

        verify(accountService)
                .reserveAmount(request.getTargetAccountId(), request.getAmount());

        verify(customerService)
                .getCustomerById(account.getCustomerId());

        verify(transactionService)
                .createTransaction(any(TransactionDTO.class));

        verifyNoInteractions(inboxService);
    }

    //Inbox entry creation failed for withdrawal(Inbox enqueue failed)
    @Test
    void processWithdrawalRequest_ShouldThrowException_WhenEnqueueFails() {

        // Arrange
        WithdrawRequestDTO request = WithdrawRequestDTO.builder()
                .targetAccountId("ACCOUNT_1")
                .amount(new BigDecimal("500"))
                .build();

        AccountDTO account = AccountDTO.builder()
                .customerId("CUSTOMER_1")
                .build();

        CustomerDTO customer = CustomerDTO.builder()
                .nationalId("NATIONAL_123")
                .build();

        TransactionDTO transaction = TransactionDTO.builder()
                .id("TRANSACTION_1")
                .build();

        when(accountService.reserveAmount(
                request.getTargetAccountId(),
                request.getAmount()))
                .thenReturn(account);

        when(customerService.getCustomerById(account.getCustomerId()))
                .thenReturn(customer);

        when(transactionService.createTransaction(any(TransactionDTO.class)))
                .thenReturn(transaction);

        when(inboxService.enqueueWithdrawal(
                request,
                transaction.getId(),
                customer.getNationalId()))
                .thenThrow(new RuntimeException("Inbox Failure"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> paymentOrchestrator.processWithdrawalRequest(request)
        );

        assertEquals("Inbox Failure", exception.getMessage());

        verify(accountService)
                .reserveAmount(request.getTargetAccountId(), request.getAmount());

        verify(customerService)
                .getCustomerById(account.getCustomerId());

        verify(transactionService)
                .createTransaction(any(TransactionDTO.class));

        verify(inboxService)
                .enqueueWithdrawal(
                        request,
                        transaction.getId(),
                        customer.getNationalId());
    }
}
