package com.bank.server.service;

import com.bank.server.entity.LogEntry;
import com.bank.server.enums.LogType;
import com.bank.server.exception.IllegalArgumentException;
import com.bank.server.repository.LogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoggerServiceTest {

    @Mock
    private LogRepository logRepository;

    @InjectMocks
    private LoggerService loggerService;

    @Test
    void log_shouldSaveLogEntry_whenInputIsValid() {

        // Act
        loggerService.log(
                "AUTH_LOGIN",
                "User logged in successfully",
                LogType.SUCCESS
        );

        // Capture the object passed to repository.save()
        // catch what was passed, check if it's correct
        // we are getting ready with our box to catch the log entry here.
        ArgumentCaptor<LogEntry> captor = ArgumentCaptor.forClass(LogEntry.class);

        // making sure that we caught the log entry
        verify(logRepository).save(captor.capture());
        // now give me captured and saved log Entry
        LogEntry savedLog = captor.getValue();

        // Assert
        assertNotNull(savedLog.getId());  // we are making sure that it's not null
        assertDoesNotThrow(() -> UUID.fromString(savedLog.getId())); // checks if its valid UUID format
        // checking if these both values match
        assertEquals(
                "AUTH_LOGIN",
                savedLog.getAction()
        );
        // get the same details, both should match
        assertEquals(
                "User logged in successfully",
                savedLog.getDetails()
        );
        // get the status to be the same one
        assertEquals(
                LogType.SUCCESS,
                savedLog.getStatus()
        );
        // get the customer
        assertNull(savedLog.getCustomer());
    }

    @Test
    void log_shouldUseSuccessStatus_whenStatusIsNull() {

        // Act
        loggerService.log(
                "CUSTOMER_CREATED",
                "Customer created successfully",
                null
        );
        ArgumentCaptor<LogEntry> captor =
                ArgumentCaptor.forClass(LogEntry.class);

        verify(logRepository).save(captor.capture());

        LogEntry savedLog = captor.getValue();
        // Assert
        assertEquals(
                LogType.SUCCESS,
                savedLog.getStatus()
        );
    }

    @Test
    void log_shouldThrowException_whenActionIsNull() {
        // Act and assert
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