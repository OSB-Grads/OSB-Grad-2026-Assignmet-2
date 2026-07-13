package com.bank.server.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(String message) {
        super(message);
    }
}
