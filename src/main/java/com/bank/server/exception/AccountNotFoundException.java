package com.bank.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import lombok.Getter;

@Getter
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class AccountNotFoundException extends RuntimeException {
    
    private final String code;
    public AccountNotFoundException(String code,String message) {
        super(message);
        this.code=code;
    }
}