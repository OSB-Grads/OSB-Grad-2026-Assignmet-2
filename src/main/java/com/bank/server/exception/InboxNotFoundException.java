package com.bank.server.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class InboxNotFoundException extends RuntimeException {
    private final String code;
    public InboxNotFoundException(String code, String message) {
        super(message);
        this.code = code;
    }
}
