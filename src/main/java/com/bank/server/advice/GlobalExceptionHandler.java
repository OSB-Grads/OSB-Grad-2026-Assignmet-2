package com.bank.server.advice;

import com.bank.server.enums.LogType;
import com.bank.server.exception.InvalidCredentialsException;
import com.bank.server.exception.TransactionNotFoundException;
import com.bank.server.exception.UsernameAlreadyExistsException;
import com.bank.server.service.LoggerService;
import com.bank.server.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.bank.server.dto.response.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;
import com.bank.server.dto.response.LogResponse;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final LoggerService loggerService;
    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTransactionNotFound(TransactionNotFoundException ex) {

        log.warn("{}", ex.getMessage());

        loggerService.log(
                ex.getCode(),
                ex.getMessage(),
                LogType.ERROR
        );

        ErrorResponse error = ErrorResponse.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException ex) {

        log.warn("{}", ex.getMessage());

        loggerService.log(
                ex.getCode(),
                ex.getMessage(),
                LogType.ERROR
        );

        ErrorResponse error = ErrorResponse.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUsernameAlreadyExist(
            UsernameAlreadyExistsException ex) {

        log.warn("{}", ex.getMessage());

        loggerService.log(
                ex.getCode(),
                ex.getMessage(),
                LogType.ERROR
        );

        ErrorResponse error = ErrorResponse.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException ex) {

        log.warn("{}", ex.getMessage());

        loggerService.log(
                ex.getCode(),
                ex.getMessage(),
                LogType.ERROR
        );

        ErrorResponse error = ErrorResponse.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    @ExceptionHandler(SameAccountTransferException.class)
    public ResponseEntity<ErrorResponse> handleSameAccountTransfer(SameAccountTransferException ex) {
        log.warn("{}", ex.getMessage());

        loggerService.log(
                ex.getCode(),
                ex.getMessage(),
                LogType.ERROR
        );
        ErrorResponse error = ErrorResponse.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    @ExceptionHandler(InvalidTransferAmountException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTransferAmount(
            InvalidTransferAmountException ex
    ) {
        log.warn("{}", ex.getMessage());

        loggerService.log(
                ex.getCode(),
                ex.getMessage(),
                LogType.ERROR
        );
        ErrorResponse error = ErrorResponse.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .build();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalance(InsufficientBalanceException ex) {

        log.warn("{}", ex.getMessage());

        loggerService.log(
                ex.getCode(),
                ex.getMessage(),
                LogType.ERROR
        );
        ErrorResponse error = ErrorResponse.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }
    @ExceptionHandler(AccountOwnershipException.class)
    public ResponseEntity<ErrorResponse> handleAccountOwnership(
            AccountOwnershipException ex
    ) {
        log.warn("{}", ex.getMessage());

        loggerService.log(
                ex.getCode(),
                ex.getMessage(),
                LogType.ERROR
        );
        ErrorResponse error = ErrorResponse.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .build();

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(error);
    }

    @ExceptionHandler(UnderAgeException.class)
    public ResponseEntity<ErrorResponse> handleUnderAgeException(
            UnderAgeException ex) {

        log.warn("{}", ex.getMessage());

        loggerService.log(
                ex.getCode(),
                ex.getMessage(),
                LogType.ERROR
        );

        ErrorResponse error = ErrorResponse.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex) {

        log.error("Unhandled exception occurred", ex);

        loggerService.log(
                "INTERNAL_SERVER_ERROR",
                ex.getMessage(),
                LogType.ERROR
        );

        ErrorResponse error = ErrorResponse.builder()
                .code("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(ProductNotFoundException ex)
    {
        log.warn("{}",ex.getMessage());

        loggerService.log(
                ex.getCode(),
                ex.getMessage(),
                LogType.ERROR
        );

        ErrorResponse error = ErrorResponse.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

   @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFound(
            AccountNotFoundException ex) {

        log.warn("{}", ex.getMessage());

        loggerService.log(
                ex.getCode(),
                ex.getMessage(),
                LogType.ERROR);

        ErrorResponse error = ErrorResponse.builder()
                .code(ex.getCode())
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

}