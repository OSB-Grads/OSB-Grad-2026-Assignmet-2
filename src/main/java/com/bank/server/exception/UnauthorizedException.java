package com.bank.server.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException {

    private final String code;

    public UnauthorizedException(String code, String message) {
        super(message);
        this.code = code;
    }
}