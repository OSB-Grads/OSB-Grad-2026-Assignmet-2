package com.bank.server.exception;

import lombok.Getter;

@Getter
public class InsufficientBalanceException extends RuntimeException {
    private final String code;

    public InsufficientBalanceException(
            String code,
            String message
    ) {
        super(message);
        this.code = code;
    }
}