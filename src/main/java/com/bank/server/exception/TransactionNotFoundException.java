package com.bank.server.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class TransactionNotFoundException extends RuntimeException {

    private final String code;

    public TransactionNotFoundException(String code, String message) {
        super(message);
        this.code = code;
    }
}