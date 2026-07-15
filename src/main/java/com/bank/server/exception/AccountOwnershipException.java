package com.bank.server.exception;

public class AccountOwnershipException extends RuntimeException {

    public AccountOwnershipException(String message) {
        super(message);
    }
}