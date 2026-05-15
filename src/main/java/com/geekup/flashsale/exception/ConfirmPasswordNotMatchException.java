package com.geekup.flashsale.exception;

public class ConfirmPasswordNotMatchException extends RuntimeException{
    public ConfirmPasswordNotMatchException(String message) {
        super(message);
    }
}
