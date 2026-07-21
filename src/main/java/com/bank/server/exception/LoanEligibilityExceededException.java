package com.bank.server.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class LoanEligibilityExceededException extends RuntimeException {

    private final String code;

    public LoanEligibilityExceededException(String code, String message) {
        super(message);
        this.code = code;
    }
}