package com.bank.server.exception;

import lombok.Getter;

@Getter
public class AccountOwnershipException extends RuntimeException {
    private final String code;
    public AccountOwnershipException(String code, String message) {
        super(message);
        this.code=code;
    }
}