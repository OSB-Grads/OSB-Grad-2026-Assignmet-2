package com.bank.server.service;

import com.bank.server.dto.AccountDTO;
import com.bank.server.dto.ViewAccountResponseDTO;
import com.bank.server.entity.Account;
import com.bank.server.entity.Product;
import com.bank.server.enums.LogType;
import com.bank.server.exception.AccountNotFoundException;
import com.bank.server.exception.InsufficientBalanceException;
import com.bank.server.exception.ProductNotFoundException;
import com.bank.server.mapper.AccountMapper;
import com.bank.server.repository.AccountRepository;
import com.bank.server.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private LoggerService loggerService;

    @InjectMocks
    private AccountService accountService;


    @Test
    void shouldReturnAllAccountsForCustomer() {

        Account account = new Account();
        account.setAccountNumber("ACC001");

        ViewAccountResponseDTO dto = new ViewAccountResponseDTO();
        dto.setAccountNumber("ACC001");

        when(accountRepository.getAccountsWithProductByCustomerId("CUST001"))
                .thenReturn(List.of(account));

        when(accountMapper.toViewDto(account))
                .thenReturn(dto);

        List<ViewAccountResponseDTO> result = accountService.getAllAccountsForCustomer("CUST001");

        assertEquals(1, result.size());
        assertEquals("ACC001", result.get(0).getAccountNumber());

        verify(accountRepository)
                .getAccountsWithProductByCustomerId("CUST001");

        verify(accountMapper)
                .toViewDto(account);

        verify(loggerService)
                .log(eq("FETCH_ALL_ACCOUNTS"),
                        anyString(),
                        eq(LogType.SUCCESS));
    }

    @Test
    void shouldThrowExceptionWhenCustomerHasNoAccounts() {

        when(accountRepository.getAccountsWithProductByCustomerId("CUST001"))
                .thenReturn(Collections.emptyList());

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.getAllAccountsForCustomer("CUST001"));

        verify(loggerService, never())
                .log(any(), any(), any());
    }

    @Test
    void shouldReturnSingleAccount() {

        Account account = new Account();
        account.setAccountNumber("ACC001");

        ViewAccountResponseDTO dto = new ViewAccountResponseDTO();
        dto.setAccountNumber("ACC001");

        when(accountRepository.findAll())
                .thenReturn(List.of(account));

        when(accountMapper.toViewDto(account))
                .thenReturn(dto);

        ViewAccountResponseDTO result = accountService.getAccountForAccountNumber(
                "CUST001",
                "ACC001");

        assertEquals("ACC001", result.getAccountNumber());

        verify(accountMapper).toViewDto(account);

        verify(loggerService)
                .log(eq("FETCH_ACCOUNT"),
                        anyString(),
                        eq(LogType.SUCCESS));
    }

    @Test
    void shouldThrowExceptionWhenAccountNotFound() {

        when(accountRepository.findAll())
                .thenReturn(Collections.emptyList());

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.getAccountForAccountNumber(
                        "CUST001",
                        "ACC001"));
    }


    @Test
    void shouldCreateAccountSuccessfully() {

        AccountDTO dto = new AccountDTO();
        dto.setProductId("PROD001");

        Product product = new Product();

        Account account = new Account();

        Account savedAccount = new Account();
        savedAccount.setAccountNumber("ACC001");

        AccountDTO savedDto = new AccountDTO();
        savedDto.setAccountNumber("ACC001");

        when(productRepository.findById("PROD001"))
                .thenReturn(Optional.of(product));

        when(accountMapper.toEntity(dto))
                .thenReturn(account);

        when(accountRepository.save(any(Account.class)))
                .thenReturn(savedAccount);

        when(accountMapper.toDto(savedAccount))
                .thenReturn(savedDto);

        AccountDTO result = accountService.createAccount(dto);

        assertEquals("ACC001",
                result.getAccountNumber());

        verify(productRepository).findById("PROD001");

        verify(accountRepository)
                .save(any(Account.class));

        verify(loggerService)
                .log(eq("CREATE_ACCOUNT"),
                        anyString(),
                        eq(LogType.SUCCESS));
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {

        AccountDTO dto = new AccountDTO();
        dto.setProductId("INVALID");

        when(productRepository.findById("INVALID"))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> accountService.createAccount(dto));

        verify(accountRepository, never())
                .save(any());
    }

    @Test
    void shouldReserveAmountSuccessfully() {

        Account account = new Account();
        account.setId("ACC001");
        account.setBalance(new BigDecimal("1000"));

        AccountDTO dto = new AccountDTO();
        dto.setBalance(new BigDecimal("700"));

        when(accountRepository.findById("ACC001"))
                .thenReturn(Optional.of(account));

        when(accountRepository.save(account))
                .thenReturn(account);

        when(accountMapper.toDto(account))
                .thenReturn(dto);

        AccountDTO result = accountService.reserveAmount(
                "ACC001",
                new BigDecimal("300"));

        assertEquals(
                new BigDecimal("700"),
                account.getBalance());

        verify(accountRepository).save(account);
    }

    @Test
    void shouldThrowExceptionWhenInsufficientBalance() {

        Account account = new Account();
        account.setBalance(new BigDecimal("200"));

        when(accountRepository.findById("ACC001"))
                .thenReturn(Optional.of(account));

        assertThrows(
                InsufficientBalanceException.class,
                () -> accountService.reserveAmount(
                        "ACC001",
                        new BigDecimal("500")));

        verify(accountRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenReserveAccountNotFound() {

        when(accountRepository.findById("ACC001"))
                .thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.reserveAmount(
                        "ACC001",
                        BigDecimal.TEN));

        verify(accountRepository, never()).save(any());
    }

    @Test
    void shouldCreditAmountSuccessfully() {

        Account account = new Account();
        account.setBalance(new BigDecimal("1000"));

        AccountDTO dto = new AccountDTO();
        dto.setBalance(new BigDecimal("1500"));

        when(accountRepository.findById("ACC001"))
                .thenReturn(Optional.of(account));

        when(accountRepository.save(account))
                .thenReturn(account);

        when(accountMapper.toDto(account))
                .thenReturn(dto);

        AccountDTO result = accountService.creditAmount(
                "ACC001",
                new BigDecimal("500"));

        assertEquals(
                new BigDecimal("1500"),
                account.getBalance());

        verify(accountRepository).save(account);
    }

    @Test
    void shouldThrowExceptionWhenCreditAccountNotFound() {

        when(accountRepository.findById("ACC001"))
                .thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.creditAmount(
                        "ACC001",
                        BigDecimal.ONE));

        verify(accountRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenAccountForUpdateNotFound() {

        when(accountRepository.findByAccountNumberForUpdate("ACC001"))
                .thenReturn(Optional.empty());

        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.getAccountForUpdate("ACC001"));
    }

    @Test
    void shouldTransferAmountSuccessfully() {

        Account source = new Account();
        source.setId("ACC001");
        source.setBalance(new BigDecimal("1000"));

        Account destination = new Account();
        destination.setId("ACC002");
        destination.setBalance(new BigDecimal("400"));

        accountService.transferAmount(
                source,
                destination,
                new BigDecimal("300"));

        assertEquals(new BigDecimal("700"), source.getBalance());
        assertEquals(new BigDecimal("700"), destination.getBalance());

        verify(accountRepository).save(source);
        verify(accountRepository).save(destination);
    }
}
