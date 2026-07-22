package com.bank.server.service;

import com.bank.server.entity.Auth;
import com.bank.server.entity.Customer;
import com.bank.server.entity.LogEntry;
import com.bank.server.enums.LogType;
import com.bank.server.enums.Role;
import com.bank.server.exception.IllegalArgumentException;
import com.bank.server.repository.CustomerRepository;
import com.bank.server.repository.LogRepository;
import com.bank.server.security.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggerServiceTest {
    @Mock
    private LogRepository logRepository;
    @Mock
    private CustomerRepository customerRepository;
    @InjectMocks
    private LoggerService loggerService;
    @BeforeEach
    void setUp() {
        Auth auth = new Auth();
        auth.setId("customer-1");
        auth.setUsername("testuser");
        auth.setPasswordHash("password");
        auth.setRole(Role.CUSTOMER);
        CustomUserDetails userDetails = new CustomUserDetails(auth);
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
    @Test
    void log_shouldSaveLogEntry_whenInputIsValid() {
        Customer customer = new Customer();
        customer.setId("customer-1");
        when(customerRepository.findById("customer-1"))
                .thenReturn(Optional.of(customer));
        loggerService.log(
                "AUTH_LOGIN",
                "User logged in successfully",
                LogType.SUCCESS
        );
        ArgumentCaptor<LogEntry> captor = ArgumentCaptor.forClass(LogEntry.class);
        verify(logRepository).save(captor.capture());
        LogEntry savedLog = captor.getValue();
        assertNotNull(savedLog.getId());
        assertDoesNotThrow(() -> UUID.fromString(savedLog.getId()));
        assertEquals(
                "AUTH_LOGIN",
                savedLog.getAction()
        );
        assertEquals(
                "User logged in successfully",
                savedLog.getDetails()
        );
        assertEquals(
                LogType.SUCCESS,
                savedLog.getStatus()
        );
        assertNotNull(savedLog.getCustomer());
        assertEquals("customer-1", savedLog.getCustomer().getId());
    }
    @Test
    void log_shouldUseSuccessStatus_whenStatusIsNull() {
        Customer customer = new Customer();
        customer.setId("customer-1");
        when(customerRepository.findById("customer-1"))
                .thenReturn(Optional.of(customer));
        loggerService.log(
                "CUSTOMER_CREATED",
                "Customer created successfully",
                null
        );
        ArgumentCaptor<LogEntry> captor =
                ArgumentCaptor.forClass(LogEntry.class);
        verify(logRepository).save(captor.capture());
        LogEntry savedLog = captor.getValue();
        assertEquals(
                LogType.SUCCESS,
                savedLog.getStatus()
        );
    }
    @Test
    void log_shouldThrowException_whenActionIsNull() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> loggerService.log(
                                null,
                                "Some message",
                                LogType.ERROR
                        )
                );
        assertEquals(
                "Log action cannot be empty",
                exception.getMessage()
        );
        verifyNoInteractions(logRepository);
    }
    @Test
    void log_shouldThrowException_whenActionIsBlank() {
        assertThrows(
                IllegalArgumentException.class,
                () -> loggerService.log(
                        "   ",
                        "Some message",
                        LogType.ERROR
                )
        );
        verifyNoInteractions(logRepository);
    }
    @Test
    void log_shouldThrowException_whenMessageIsNull() {
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> loggerService.log(
                                "AUTH_LOGIN",
                                null,
                                LogType.ERROR
                        )
                );
        assertEquals(
                "Log message cannot be empty",
                exception.getMessage()
        );
        verifyNoInteractions(logRepository);
    }

    @Test
    void log_shouldThrowException_whenMessageIsBlank() {
        assertThrows(
                IllegalArgumentException.class,
                () -> loggerService.log(
                        "AUTH_LOGIN",
                        "   ",
                        LogType.ERROR
                )
        );
        verifyNoInteractions(logRepository);
    }
}