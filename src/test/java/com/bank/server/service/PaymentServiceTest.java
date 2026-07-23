package com.bank.server.service;

import com.bank.server.dto.InboxDTO;
import com.bank.server.enums.InboxMessageType;
import com.bank.server.enums.LogType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    @Mock
    private LoggerService loggerService;

    @Mock
    private InboxService inboxService;

    @Mock
    private PaymentProcessorService paymentProcessorService;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void processDeposits_ShouldProcessSingleDepositSuccessfully() {

        InboxDTO deposit = InboxDTO.builder()
                .id("INBOX_1")
                .messageType(InboxMessageType.DEPOSIT)
                .build();

        when(inboxService.findPendingDeposits())
                .thenReturn(List.of(deposit));

        paymentService.processDeposits();

        // Verify pending deposits were fetched
        verify(inboxService).findPendingDeposits();

        // Verify processor was called
        verify(paymentProcessorService)
                .processDeposit(deposit);

        verify(loggerService).log(
                eq("PAYMENT_PROCESS"),
                contains("processed successfully"),
                eq(LogType.SUCCESS)
        );

        // Ensure withdrawal processor was never called
        verify(paymentProcessorService, never())
                .processWithdrawalResponse(any());

        verifyNoMoreInteractions(paymentProcessorService);
    }

    @Test
    void processDeposits_ShouldProcessMultipleDepositsSuccessfully() {

        InboxDTO deposit1 = InboxDTO.builder()
                .id("INBOX_1")
                .messageType(InboxMessageType.DEPOSIT)
                .build();

        InboxDTO deposit2 = InboxDTO.builder()
                .id("INBOX_2")
                .messageType(InboxMessageType.DEPOSIT)
                .build();

        InboxDTO deposit3 = InboxDTO.builder()
                .id("INBOX_3")
                .messageType(InboxMessageType.DEPOSIT)
                .build();

        when(inboxService.findPendingDeposits())
                .thenReturn(List.of(deposit1, deposit2, deposit3));

        paymentService.processDeposits();

        verify(inboxService).findPendingDeposits();

        verify(paymentProcessorService).processDeposit(deposit1);
        verify(paymentProcessorService).processDeposit(deposit2);
        verify(paymentProcessorService).processDeposit(deposit3);

        verify(paymentProcessorService, times(3))
                .processDeposit(any(InboxDTO.class));

        verify(loggerService, times(3))
                .log(
                        eq("PAYMENT_PROCESS"),
                        contains("processed successfully"),
                        eq(LogType.SUCCESS)
                );

        verify(paymentProcessorService, never())
                .processWithdrawalResponse(any());
    }

    @Test
    void processDeposits_ShouldDoNothing_WhenNoPendingDeposits() {

        when(inboxService.findPendingDeposits())
                .thenReturn(Collections.emptyList());

        paymentService.processDeposits();

        verify(inboxService).findPendingDeposits();

        verifyNoInteractions(paymentProcessorService);

        verifyNoInteractions(loggerService);
    }

    @Test
    void processDeposits_ShouldLogFailure_WhenDepositProcessingFails() {

        InboxDTO deposit = InboxDTO.builder()
                .id("INBOX_1")
                .messageType(InboxMessageType.DEPOSIT)
                .build();

        when(inboxService.findPendingDeposits())
                .thenReturn(List.of(deposit));

        doThrow(new RuntimeException("Processing Failed"))
                .when(paymentProcessorService)
                .processDeposit(deposit);

        paymentService.processDeposits();

        verify(inboxService).findPendingDeposits();

        verify(paymentProcessorService)
                .processDeposit(deposit);

        verify(loggerService).log(
                eq("PAYMENT_PROCESS"),
                contains("Failed to process inbox"),
                eq(LogType.FAILURE)
        );

        verify(loggerService, never()).log(
                eq("PAYMENT_PROCESS"),
                contains("processed successfully"),
                eq(LogType.SUCCESS)
        );

        verify(paymentProcessorService, never())
                .processWithdrawalResponse(any());
    }

    @Test
    void processWithdrawals_ShouldProcessSingleWithdrawalSuccessfully() {

        InboxDTO withdrawal = InboxDTO.builder()
                .id("INBOX_1")
                .messageType(InboxMessageType.WITHDRAWAL_RESPONSE)
                .build();

        when(inboxService.findPendingWithdrawalResponses())
                .thenReturn(List.of(withdrawal));

        paymentService.processWithdrawals();

        verify(inboxService).findPendingWithdrawalResponses();

        verify(paymentProcessorService)
                .processWithdrawalResponse(withdrawal);

        verify(loggerService).log(
                eq("PAYMENT_PROCESS"),
                contains("processed successfully"),
                eq(LogType.SUCCESS)
        );

        verify(paymentProcessorService, never())
                .processDeposit(any());

        verifyNoMoreInteractions(paymentProcessorService);
    }
    @Test
    void processWithdrawals_ShouldProcessMultipleWithdrawalsSuccessfully() {


        InboxDTO withdrawal1 = InboxDTO.builder()
                .id("INBOX_1")
                .messageType(InboxMessageType.WITHDRAWAL_RESPONSE)
                .build();

        InboxDTO withdrawal2 = InboxDTO.builder()
                .id("INBOX_2")
                .messageType(InboxMessageType.WITHDRAWAL_RESPONSE)
                .build();

        InboxDTO withdrawal3 = InboxDTO.builder()
                .id("INBOX_3")
                .messageType(InboxMessageType.WITHDRAWAL_RESPONSE)
                .build();

        when(inboxService.findPendingWithdrawalResponses())
                .thenReturn(List.of(withdrawal1, withdrawal2, withdrawal3));

        paymentService.processWithdrawals();

        verify(inboxService).findPendingWithdrawalResponses();

        verify(paymentProcessorService).processWithdrawalResponse(withdrawal1);
        verify(paymentProcessorService).processWithdrawalResponse(withdrawal2);
        verify(paymentProcessorService).processWithdrawalResponse(withdrawal3);

        verify(paymentProcessorService, times(3))
                .processWithdrawalResponse(any(InboxDTO.class));

        verify(loggerService, times(3))
                .log(
                        eq("PAYMENT_PROCESS"),
                        contains("processed successfully"),
                        eq(LogType.SUCCESS)
                );

        verify(paymentProcessorService, never())
                .processDeposit(any());
    }
    @Test
    void processWithdrawals_ShouldDoNothing_WhenNoPendingWithdrawals() {

        when(inboxService.findPendingWithdrawalResponses())
                .thenReturn(Collections.emptyList());

        paymentService.processWithdrawals();

        verify(inboxService).findPendingWithdrawalResponses();

        verifyNoInteractions(paymentProcessorService);

        verifyNoInteractions(loggerService);
    }
    @Test
    void processWithdrawals_ShouldLogFailure_WhenWithdrawalProcessingFails() {

        InboxDTO withdrawal = InboxDTO.builder()
                .id("INBOX_1")
                .messageType(InboxMessageType.WITHDRAWAL_RESPONSE)
                .build();

        when(inboxService.findPendingWithdrawalResponses())
                .thenReturn(List.of(withdrawal));

        doThrow(new RuntimeException("Processing Failed"))
                .when(paymentProcessorService)
                .processWithdrawalResponse(withdrawal);

        paymentService.processWithdrawals();

        verify(inboxService).findPendingWithdrawalResponses();

        verify(paymentProcessorService)
                .processWithdrawalResponse(withdrawal);

        verify(loggerService).log(
                eq("PAYMENT_PROCESS"),
                contains("Failed to process inbox"),
                eq(LogType.FAILURE)
        );

        verify(loggerService, never()).log(
                eq("PAYMENT_PROCESS"),
                contains("processed successfully"),
                eq(LogType.SUCCESS)
        );

        verify(paymentProcessorService, never())
                .processDeposit(any());
    }
}
