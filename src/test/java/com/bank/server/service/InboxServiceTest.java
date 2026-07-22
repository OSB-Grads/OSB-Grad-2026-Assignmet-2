package com.bank.server.service;

import com.bank.server.dto.InboxDTO;
import com.bank.server.dto.request.WithdrawRequestDTO;
import com.bank.server.dto.response.InboxResponseDTO;
import com.bank.server.entity.Inbox;
import com.bank.server.enums.InboxMessageType;
import com.bank.server.enums.InboxStatus;
import com.bank.server.enums.LogType;
import com.bank.server.exception.InboxNotFoundException;
import com.bank.server.mapper.InboxMapper;
import com.bank.server.repository.InboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InboxServiceTest {

    @Mock
    private InboxRepository inboxRepository;
    @Mock
    private InboxMapper inboxMapper;
    @Mock
    private LoggerService loggerService;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private InboxService inboxService;

    @Test
    void enqueueWithdrawal_ShouldQueueWithdrawalSuccessfully() throws JsonProcessingException {
        WithdrawRequestDTO request =
                WithdrawRequestDTO.builder()
                        .targetAccountId("ACC123")
                        .amount(new BigDecimal("500"))
                        .build();

        String transactionId = "txn123";
        String nationalId = "987654321543";

        Inbox savedInbox = Inbox.builder()
                .id("inbox123")
                .transactionId(transactionId)
                .status(InboxStatus.PENDING)
                .build();

        when(objectMapper.writeValueAsString(any()))
                .thenReturn("{json}");

        when(inboxRepository.save(any(Inbox.class)))
                .thenReturn(savedInbox);

        InboxResponseDTO response =
                inboxService.enqueueWithdrawal(
                        request,
                        transactionId,
                        nationalId
                );

        assertNotNull(response);

        assertEquals("inbox123", response.getId());

        assertEquals(transactionId, response.getTransactionId());

        assertEquals(InboxStatus.PENDING, response.getStatus());

        assertEquals(
                "Withdrawal request queued successfully.",
                response.getMessage()
        );

        verify(inboxRepository).save(any(Inbox.class));

        verify(loggerService).log(
                eq("QUEUE_WITHDRAWAL"),
                contains("Withdrawal request queued successfully"),
                eq(LogType.SUCCESS)
        );
    }

    @Test
    void enqueueWithdrawal_ShouldThrowException_WhenSerializationFails() throws Exception {

        WithdrawRequestDTO request = WithdrawRequestDTO.builder()
                .targetAccountId("ACC123")
                .amount(new BigDecimal("500"))
                .build();

        String transactionId = "txn123";
        String nationalId = "987654321";

        when(objectMapper.writeValueAsString(any()))
                .thenThrow(new JsonProcessingException("Serialization failed") {});

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> inboxService.enqueueWithdrawal(
                        request,
                        transactionId,
                        nationalId
                )
        );

        assertEquals(
                "Failed to serialize withdrawal payload",
                exception.getMessage()
        );

        verify(inboxRepository, never()).save(any());
        verify(loggerService, never()).log(any(), any(), any());
    }
    @Test
    void findPendingEntries_ShouldReturnPendingEntries() {

        // Arrange
        Inbox inbox1 = Inbox.builder()
                .id("1")
                .status(InboxStatus.PENDING)
                .build();

        Inbox inbox2 = Inbox.builder()
                .id("2")
                .status(InboxStatus.PENDING)
                .build();

        List<Inbox> pendingEntries = List.of(inbox1, inbox2);

        when(inboxRepository.findByStatus(InboxStatus.PENDING))
                .thenReturn(pendingEntries);

        // Act
        List<Inbox> result = inboxService.findPendingEntries();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("1", result.get(0).getId());
        assertEquals("2", result.get(1).getId());

        verify(inboxRepository,times(1))
                .findByStatus(InboxStatus.PENDING);
    }

    @Test
    void findPendingEntries_ShouldReturnEmptyList_WhenNoPendingEntriesExist() {

        // Arrange
        when(inboxRepository.findByStatus(InboxStatus.PENDING))
                .thenReturn(Collections.emptyList());

        // Act
        List<Inbox> result = inboxService.findPendingEntries();

        // Assert
        assertNotNull(result);

        assertTrue(result.isEmpty());

        verify(inboxRepository)
                .findByStatus(InboxStatus.PENDING);
    }
    @Test
    void findPendingDeposits_ShouldReturnPendingDeposits() {

        // Arrange
        Inbox inbox1 = Inbox.builder()
                .id("1")
                .messageType(InboxMessageType.DEPOSIT)
                .status(InboxStatus.PENDING)
                .build();

        Inbox inbox2 = Inbox.builder()
                .id("2")
                .messageType(InboxMessageType.DEPOSIT)
                .status(InboxStatus.PENDING)
                .build();

        InboxDTO dto1 = InboxDTO.builder()
                .id("1")
                .build();

        InboxDTO dto2 = InboxDTO.builder()
                .id("2")
                .build();

        when(inboxRepository.findByStatusAndMessageTypeOrderByCreatedAtAsc(
                InboxStatus.PENDING,
                InboxMessageType.DEPOSIT))
                .thenReturn(List.of(inbox1, inbox2));

        when(inboxMapper.toDTO(inbox1)).thenReturn(dto1);
        when(inboxMapper.toDTO(inbox2)).thenReturn(dto2);

        // Act
        List<InboxDTO> result = inboxService.findPendingDeposits();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("1", result.get(0).getId());
        assertEquals("2", result.get(1).getId());

        verify(inboxRepository)
                .findByStatusAndMessageTypeOrderByCreatedAtAsc(
                        InboxStatus.PENDING,
                        InboxMessageType.DEPOSIT);

        verify(inboxMapper).toDTO(inbox1);
        verify(inboxMapper).toDTO(inbox2);

        verify(loggerService).log(
                eq("FETCH_PENDING_DEPOSITS"),
                contains("Fetched pending deposit inbox entries"),
                eq(LogType.SUCCESS)
        );
    }
    @Test
    void findPendingDeposits_ShouldReturnEmptyList_WhenNoPendingDepositsExist() {

        // Arrange
        when(inboxRepository.findByStatusAndMessageTypeOrderByCreatedAtAsc(
                InboxStatus.PENDING,
                InboxMessageType.DEPOSIT))
                .thenReturn(Collections.emptyList());

        // Act
        List<InboxDTO> result = inboxService.findPendingDeposits();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(inboxRepository)
                .findByStatusAndMessageTypeOrderByCreatedAtAsc(
                        InboxStatus.PENDING,
                        InboxMessageType.DEPOSIT);

        verify(loggerService).log(
                eq("FETCH_PENDING_DEPOSITS"),
                contains("Fetched pending deposit inbox entries"),
                eq(LogType.SUCCESS)
        );

        verifyNoInteractions(inboxMapper);
    }
    @Test
    void findPendingWithdrawalResponses_ShouldReturnPendingWithdrawalResponses() {

        // Arrange
        Inbox inbox1 = Inbox.builder()
                .id("1")
                .messageType(InboxMessageType.WITHDRAWAL_RESPONSE)
                .status(InboxStatus.PENDING)
                .build();

        Inbox inbox2 = Inbox.builder()
                .id("2")
                .messageType(InboxMessageType.WITHDRAWAL_RESPONSE)
                .status(InboxStatus.PENDING)
                .build();

        InboxDTO dto1 = InboxDTO.builder()
                .id("1")
                .build();

        InboxDTO dto2 = InboxDTO.builder()
                .id("2")
                .build();

        when(inboxRepository.findByStatusAndMessageTypeOrderByCreatedAtAsc(
                InboxStatus.PENDING,
                InboxMessageType.WITHDRAWAL_RESPONSE))
                .thenReturn(List.of(inbox1, inbox2));

        when(inboxMapper.toDTO(inbox1)).thenReturn(dto1);
        when(inboxMapper.toDTO(inbox2)).thenReturn(dto2);

        // Act
        List<InboxDTO> result = inboxService.findPendingWithdrawalResponses();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("1", result.get(0).getId());
        assertEquals("2", result.get(1).getId());

        verify(inboxRepository)
                .findByStatusAndMessageTypeOrderByCreatedAtAsc(
                        InboxStatus.PENDING,
                        InboxMessageType.WITHDRAWAL_RESPONSE);

        verify(inboxMapper).toDTO(inbox1);
        verify(inboxMapper).toDTO(inbox2);

        verify(loggerService).log(
                eq("FETCH_PENDING_WITHDRAWAL_RESPONSES"),
                contains("Fetched pending withdrawals response inbox entries"),
                eq(LogType.SUCCESS)
        );
    }
    @Test
    void findPendingWithdrawalResponses_ShouldReturnEmptyList_WhenNoResponsesExist() {

        // Arrange
        when(inboxRepository.findByStatusAndMessageTypeOrderByCreatedAtAsc(
                InboxStatus.PENDING,
                InboxMessageType.WITHDRAWAL_RESPONSE))
                .thenReturn(Collections.emptyList());

        // Act
        List<InboxDTO> result = inboxService.findPendingWithdrawalResponses();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(inboxRepository)
                .findByStatusAndMessageTypeOrderByCreatedAtAsc(
                        InboxStatus.PENDING,
                        InboxMessageType.WITHDRAWAL_RESPONSE);

        verify(loggerService).log(
                eq("FETCH_PENDING_WITHDRAWAL_RESPONSES"),
                contains("Fetched pending withdrawals response inbox entries"),
                eq(LogType.SUCCESS)
        );

        verifyNoInteractions(inboxMapper);
    }
    @Test
    void deleteById_ShouldDeleteInboxSuccessfully() {

        // Arrange
        String inboxId = "inbox123";

        Inbox inbox = Inbox.builder()
                .id(inboxId)
                .status(InboxStatus.PENDING)
                .build();

        when(inboxRepository.findById(inboxId))
                .thenReturn(Optional.of(inbox));

        // Act
        inboxService.deleteById(inboxId);

        // Assert
        verify(inboxRepository).findById(inboxId);

        verify(inboxRepository).delete(inbox);

        verify(loggerService).log(
                eq("DELETE_INBOX_ENTRY"),
                contains("Deleted inbox entry"),
                eq(LogType.SUCCESS)
        );
    }
    @Test
    void deleteById_ShouldThrowException_WhenInboxNotFound() {

        // Arrange
        String inboxId = "inbox123";

        when(inboxRepository.findById(inboxId))
                .thenReturn(Optional.empty());

        // Act & Assert
        InboxNotFoundException exception = assertThrows(
                InboxNotFoundException.class,
                () -> inboxService.deleteById(inboxId)
        );

        assertTrue(exception.getMessage().contains(inboxId));

        verify(inboxRepository).findById(inboxId);

        verify(inboxRepository, never()).delete(any());

        verify(loggerService, never()).log(any(), any(), any());
    }
}
