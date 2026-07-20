package com.bank.server.exception;

public class CustomerNotFoundException extends ResourceNotFoundException {

    public CustomerNotFoundException(String code, String message) {
        super(code, message);
    }
}