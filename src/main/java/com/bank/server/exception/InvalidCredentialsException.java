package com.bank.server.exception;

import lombok.Getter;

@Getter
public class InvalidCredentialsException extends RuntimeException {

    private final String code;

    public InvalidCredentialsException(String code, String message) {
        super(message);
        this.code = code;
    }
}