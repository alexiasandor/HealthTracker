package com.healthtrack_device.healthtrack_device.controllers.handlers;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends AbstractException{
    private static final String MESSAGE = "Resource duplicated!";
    private static final HttpStatus HTTP_STATUS = HttpStatus.CONFLICT;

    public DuplicateResourceException(String resource) {
        super(MESSAGE, resource, HTTP_STATUS);
    }
}
