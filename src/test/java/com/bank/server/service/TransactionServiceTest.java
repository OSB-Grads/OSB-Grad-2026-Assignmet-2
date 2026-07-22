package com.bank.server.service;

import com.bank.server.dto.TransactionDTO;
import com.bank.server.dto.response.TransferResponse;
import com.bank.server.entity.Account;
import com.bank.server.entity.Customer;
import com.bank.server.entity.Transaction;
import com.bank.server.enums.TransactionStatus;
import com.bank.server.exception.TransactionNotFoundException;
import com.bank.server.mapper.TransactionMapper;
import com.bank.server.repository.AccountRepository;
import com.bank.server.repository.TransactionRepository;
import com.bank.server.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction transaction;
    private TransactionDTO transactionDTO;
    private Account sourceAccount;
    private Account destinationAccount;
    private Customer customer;

    @BeforeEach
    void setUp() {

        customer = new Customer();
        customer.setId("C001");

        sourceAccount = new Account();
        sourceAccount.setId("A001");
        sourceAccount.setAccountNumber("ACC001");
        sourceAccount.setBalance(new BigDecimal("5000"));
        sourceAccount.setCustomer(customer);

        destinationAccount = new Account();
        destinationAccount.setId("A002");
        destinationAccount.setAccountNumber("ACC002");
        destinationAccount.setBalance(new BigDecimal("8000"));

        transaction = new Transaction();
        transaction.setId("T001");
        transaction.setTransactionType("TRANSFER");
        transaction.setAmount(new BigDecimal("1000"));
        transaction.setStatus(TransactionStatus.valueOf("COMPLETED"));
        transaction.setDescription("Transfer");
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setCustomer(customer);
        transaction.setFromAccount(sourceAccount);
        transaction.setToAccount(destinationAccount);

        transactionDTO = new TransactionDTO();
        transactionDTO.setId("T001");
        transactionDTO.setCustomerId("C001");
        transactionDTO.setFromAccountId("A001");
        transactionDTO.setToAccountId("A002");
        transactionDTO.setAmount(new BigDecimal("1000"));
        transactionDTO.setStatus(TransactionStatus.valueOf("COMPLETED"));
    }

    @Test
    void shouldReturnTransactionById() {
        when(transactionRepository.findById("T001"))
                .thenReturn(Optional.of(transaction));

        when(transactionMapper.toDto(transaction))
                .thenReturn(transactionDTO);

        TransactionDTO result =
                transactionService.getTransactionById("T001");

        assertNotNull(result);
        assertEquals("T001", result.getId());

        verify(transactionRepository).findById("T001");
        verify(transactionMapper).toDto(transaction);
    }

    @Test
    void shouldThrowTransactionNotFoundException() {
        when(transactionRepository.findById("INVALID"))
                .thenReturn(Optional.empty());

        assertThrows(
                TransactionNotFoundException.class,
                () -> transactionService.getTransactionById("INVALID")
        );

        verify(transactionRepository).findById("INVALID");
        verify(transactionMapper, never()).toDto(any());
    }

    @Test
    void shouldReturnTransactionsByAccountNumber() {
        when(accountRepository.findByAccountNumber("ACC001"))
                .thenReturn(Optional.of(sourceAccount));

        when(transactionRepository.findByAccountId("A001"))
                .thenReturn(List.of(transaction));

        when(transactionMapper.toDto(transaction))
                .thenReturn(transactionDTO);

        List<TransactionDTO> result =
                transactionService.getTransactionsByAccountNumber("ACC001");

        assertEquals(1, result.size());
        assertEquals("T001", result.get(0).getId());

        verify(accountRepository)
                .findByAccountNumber("ACC001");

        verify(transactionRepository)
                .findByAccountId("A001");

        verify(transactionMapper)
                .toDto(transaction);
    }

    @Test
    void shouldThrowNoSuchElementException() {
        when(accountRepository.findByAccountNumber("ACC001"))
                .thenReturn(Optional.empty());

        assertThrows(
                NoSuchElementException.class,
                () -> transactionService.getTransactionsByAccountNumber("ACC001")
        );

        verify(accountRepository)
                .findByAccountNumber("ACC001");
    }

    @Test
    void shouldReturnTransactionsByCustomer() {
        when(transactionRepository.findByCustomerId("C001"))
                .thenReturn(List.of(transaction));

        when(transactionMapper.toDto(transaction))
                .thenReturn(transactionDTO);

        List<TransactionDTO> result =
                transactionService.getTransactionsByCustomerId("C001");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("T001", result.get(0).getId());

        verify(transactionRepository)
                .findByCustomerId("C001");

        verify(transactionMapper)
                .toDto(transaction);
    }

    @Test
    void shouldReturnEmptyTransactionListForCustomer() {
        when(transactionRepository.findByCustomerId("C001"))
                .thenReturn(Collections.emptyList());

        List<TransactionDTO> result =
                transactionService.getTransactionsByCustomerId("C001");

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(transactionRepository)
                .findByCustomerId("C001");

        verify(transactionMapper, never())
                .toDto(any());
    }

    @Test
    void shouldCreateTransferTransaction() {
        BigDecimal amount = new BigDecimal("1000");

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result =
                transactionService.createTransferTransaction(
                        sourceAccount,
                        destinationAccount,
                        amount
                );

        ArgumentCaptor<Transaction> transactionCaptor =
                ArgumentCaptor.forClass(Transaction.class);

        verify(transactionRepository)
                .save(transactionCaptor.capture());

        Transaction savedTransaction =
                transactionCaptor.getValue();

        assertNotNull(result);
        assertNotNull(savedTransaction.getId());

        assertEquals(
                "TRANSFER",
                savedTransaction.getTransactionType()
        );

        assertEquals(
                amount,
                savedTransaction.getAmount()
        );

        assertEquals(
                "COMPLETED",
                savedTransaction.getStatus()
        );

        assertEquals(
                sourceAccount,
                savedTransaction.getFromAccount()
        );

        assertEquals(
                destinationAccount,
                savedTransaction.getToAccount()
        );

        assertEquals(
                customer,
                savedTransaction.getCustomer()
        );

        assertTrue(
                savedTransaction.getDescription()
                        .startsWith("Internal transfer reference:")
        );
    }

    @Test
    void shouldCreateTransferResponse() {
        transaction.setCreatedAt(LocalDateTime.now());

        TransferResponse response =
                transactionService.toTransferResponse(
                        transaction,
                        sourceAccount,
                        destinationAccount
                );

        assertNotNull(response);

        assertEquals(
                transaction.getId(),
                response.getTransferReference()
        );

        assertEquals(
                sourceAccount.getAccountNumber(),
                response.getSourceAccountNumber()
        );

        assertEquals(
                destinationAccount.getAccountNumber(),
                response.getDestinationAccountNumber()
        );

        assertEquals(
                transaction.getAmount(),
                response.getAmount()
        );

        assertEquals(
                sourceAccount.getBalance(),
                response.getSourceBalance()
        );

        assertEquals(
                destinationAccount.getBalance(),
                response.getDestinationBalance()
        );

        assertEquals(
                transaction.getStatus(),
                response.getStatus()
        );

        assertEquals(
                transaction.getCreatedAt(),
                response.getTransferredAt()
        );

        assertEquals(
                "Transfer completed successfully",
                response.getMessage()
        );
    }
}
 