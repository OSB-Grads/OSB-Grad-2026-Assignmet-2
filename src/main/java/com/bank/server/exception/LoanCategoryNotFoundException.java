package com.bank.server.exception;

public class LoanCategoryNotFoundException extends ResourceNotFoundException {

    public LoanCategoryNotFoundException(String code, String message) {
        super(code, message);
    }
}