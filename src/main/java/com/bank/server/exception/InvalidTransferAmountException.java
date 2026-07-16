package com.bank.server.exception;

import lombok.Getter;

@Getter
public class InvalidTransferAmountException extends RuntimeException {
    private final String code;

    public InvalidTransferAmountException(
            String code,
            String message
    ) {
        super(message);
        this.code = code;
    }
}