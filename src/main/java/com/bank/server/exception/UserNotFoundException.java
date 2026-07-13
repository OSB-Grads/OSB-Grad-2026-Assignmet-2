package com.bank.server.exception;

import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends ChangeSetPersister.NotFoundException {

    public UserNotFoundException(String message) {
        super();
    }

}