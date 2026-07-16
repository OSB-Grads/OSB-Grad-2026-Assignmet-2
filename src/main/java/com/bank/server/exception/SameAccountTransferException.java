package com.bank.server.exception;

import lombok.Getter;

@Getter
public class SameAccountTransferException extends RuntimeException {
    private final String code;

    public SameAccountTransferException(
            String code,
            String message
    ) {
        super(message);
        this.code = code;
    }
}
