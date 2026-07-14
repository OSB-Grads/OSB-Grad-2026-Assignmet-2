package com.bank.server.exception;

import lombok.Getter;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends ChangeSetPersister.NotFoundException {

    private final String code;
    private final String message;

    public UserNotFoundException(String code, String message) {
        super();
        this.code = code;
        this.message = message;
    }
}