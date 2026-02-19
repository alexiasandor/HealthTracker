package com.healthtrack_user.healthtrack_user.controller.handlers;

import org.springframework.http.HttpStatus;

public class InvalidFieldFormatException extends AbstractException{
    private static final String MESSAGE = "Invalid field format!";
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_ACCEPTABLE;

    public InvalidFieldFormatException(String resource) {
        super(MESSAGE, resource, HTTP_STATUS);
    }
}
