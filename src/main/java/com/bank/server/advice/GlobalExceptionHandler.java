package com.bank.server.advice;

import com.bank.server.enums.LogType;
import com.bank.server.exception.AccountNotFoundException;
import com.bank.server.exception.TransactionNotFoundException;
import com.bank.server.exception.UserNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<String> handleAccountNotFound(
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