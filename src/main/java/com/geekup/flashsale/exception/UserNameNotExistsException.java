package com.geekup.flashsale.exception;

public class UserNameNotExistsException extends RuntimeException{
    public UserNameNotExistsException(String message) {
        super(message);
    }
}
