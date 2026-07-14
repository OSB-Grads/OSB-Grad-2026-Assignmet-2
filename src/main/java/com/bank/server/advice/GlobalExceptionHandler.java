package com.bank.server.advice;

import com.bank.server.dto.ErrorResponse;
import com.bank.server.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTransactionNotFound(
            TransactionNotFoundException ex) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("TRANSACTION_NOT_FOUND")
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException ex) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("INVALID_CREDENTIALS")
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUsernameAlreadyExist(
            UsernameAlreadyExistsException ex) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("USERNAME_ALREADY_EXISTS")
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(errorResponse);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException ex) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("USER_NOT_FOUND")
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler(UnderAgeException.class)
    public ResponseEntity<ErrorResponse> handleUnderAgeException(
            UnderAgeException ex) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("UNDER_AGE")
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred")
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
}