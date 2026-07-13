package com.bank.server.exception;

import javax.swing.text.ChangedCharSetException;

public class UsernameAlreadyExistsException extends ChangedCharSetException.UserResource {
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
