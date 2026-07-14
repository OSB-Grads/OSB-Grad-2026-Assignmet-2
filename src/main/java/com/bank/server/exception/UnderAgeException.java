package com.bank.server.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnderAgeException extends RuntimeException {

    private final String code;

    public UnderAgeException(String code, String message) {
        super(message);
        this.code = code;
    }
}
