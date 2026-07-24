package com.bank.server.orchestrator;

import com.bank.server.dto.request.TransferRequestDTO;
import com.bank.server.dto.response.TransferResponse;
import com.bank.server.entity.Account;
import com.bank.server.entity.Transaction;
import com.bank.server.enums.LogType;
import com.bank.server.enums.TransactionStatus;
import com.bank.server.exception.AccountOwnershipException;
import com.bank.server.exception.InvalidTransferAmountException;
import com.bank.server.service.AccountService;
import com.bank.server.service.LoggerService;
import com.bank.server.service.TransactionService;
import com.bank.server.utils.TransferValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferOrchestratorTest {
    private static final String CUSTOMER_ID = "customer-1";
    private static final String SOURCE_NUMBER = "ACC-001";
    private static final String DESTINATION_NUMBER = "ACC-002";
    private static final BigDecimal AMOUNT =
            new BigDecimal("500.00");

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private TransferValidator validator;

    @Mock
    private LoggerService loggerService;

    @InjectMocks
    private TransferOrchestrator transferOrchestrator;

    private TransferRequestDTO request;
    private Account sourceAccount;
    private Account destinationAccount;

    @BeforeEach
    void setUp() {
        request = new TransferRequestDTO(
                SOURCE_NUMBER,
                DESTINATION_NUMBER,
                AMOUNT);

        sourceAccount = Account.builder()
                .id("source-id")
                .accountNumber(SOURCE_NUMBER)
                .balance(new BigDecimal("2000.00"))
                .build();

        destinationAccount = Account.builder()
                .id("destination-id")
                .accountNumber(DESTINATION_NUMBER)
                .balance(new BigDecimal("1000.00"))
                .build();
    }
    @Test
    void transfer_shouldCompleteSuccessfully_whenRequestIsValid() {
        Transaction transaction = new Transaction();
        transaction.setId("transfer-123");
        transaction.setAmount(AMOUNT);

        TransferResponse expectedResponse =
                TransferResponse.builder()
                        .transferReference("transfer-123")
                        .sourceAccountNumber(SOURCE_NUMBER)
                        .destinationAccountNumber(DESTINATION_NUMBER)
                        .amount(AMOUNT)
                        .sourceBalance(new BigDecimal("1500.00"))
                        .destinationBalance(new BigDecimal("1500.00"))
                        .status(TransactionStatus.COMPLETED)
                        .message("Transfer completed successfully")
                        .build();

        when(accountService.getAccountForUpdate(SOURCE_NUMBER))
                .thenReturn(sourceAccount);

        when(accountService.getAccountForUpdate(DESTINATION_NUMBER))
                .thenReturn(destinationAccount);

        when(transactionService.createTransferTransaction(
                sourceAccount,
                destinationAccount,
                AMOUNT
        )).thenReturn(transaction);

        when(transactionService.toTransferResponse(
                transaction,
                sourceAccount,
                destinationAccount
        )).thenReturn(expectedResponse);

        TransferResponse actualResponse =
                transferOrchestrator.transfer(
                        CUSTOMER_ID,
                        request);

        assertSame(expectedResponse, actualResponse);
        assertEquals(
                "transfer-123",
                actualResponse.getTransferReference());
        assertEquals(AMOUNT, actualResponse.getAmount());

        InOrder order = inOrder(
                validator,
                accountService,
                transactionService,
                loggerService
        );

        order.verify(validator).validateRequest(request);

        order.verify(accountService).getAccountForUpdate(SOURCE_NUMBER);

        order.verify(accountService).getAccountForUpdate(DESTINATION_NUMBER);

        order.verify(validator).validateAccounts(
                CUSTOMER_ID,
                AMOUNT,
                sourceAccount,
                destinationAccount);

        order.verify(accountService).transferAmount(
                sourceAccount,
                destinationAccount,
                AMOUNT);

        order.verify(transactionService)
                .createTransferTransaction(
                        sourceAccount,
                        destinationAccount,
                        AMOUNT);

        order.verify(loggerService).log(
                "TRANSFER_COMPLETED",
                "Transfer completed successfully. Reference: transfer-123",
                LogType.SUCCESS);

        order.verify(transactionService)
                .toTransferResponse(
                        transaction,
                        sourceAccount,
                        destinationAccount);
    }

    @Test
    void transfer_shouldStop_whenRequestValidationFails() {
        InvalidTransferAmountException exception =
                new InvalidTransferAmountException(
                        "INVALID_TRANSFER_AMOUNT",
                        "Transfer amount must be greater than zero");

        doThrow(exception)
                .when(validator)
                .validateRequest(request);

        InvalidTransferAmountException thrown =
                assertThrows(InvalidTransferAmountException.class,
                        () -> transferOrchestrator.transfer(
                                CUSTOMER_ID,
                                request));

        assertEquals(
                "INVALID_TRANSFER_AMOUNT",
                thrown.getCode()
        );

        verifyNoInteractions(
                accountService,
                transactionService,
                loggerService);
    }

    @Test
    void transfer_shouldStop_whenAccountValidationFails() {
        when(accountService.getAccountForUpdate(SOURCE_NUMBER))
                .thenReturn(sourceAccount);

        when(accountService.getAccountForUpdate(DESTINATION_NUMBER))
                .thenReturn(destinationAccount);

        AccountOwnershipException exception =
                new AccountOwnershipException(
                        "ACCOUNT_OWNERSHIP_MISMATCH",
                        "Source account does not belong to the customer");

        doThrow(exception)
                .when(validator)
                .validateAccounts(
                        CUSTOMER_ID,
                        AMOUNT,
                        sourceAccount,
                        destinationAccount);

        AccountOwnershipException thrown =
                assertThrows(
                        AccountOwnershipException.class,
                        () -> transferOrchestrator.transfer(
                                CUSTOMER_ID,
                                request)
                );

        assertEquals(
                "ACCOUNT_OWNERSHIP_MISMATCH",
                thrown.getCode()
        );

        verify(accountService, never())
                .transferAmount(any(), any(), any());

        verifyNoInteractions(
                transactionService,
                loggerService);
    }
}

