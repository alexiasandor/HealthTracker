package com.healthtrack_device.healthtrack_device.controllers.handlers;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends AbstractException{
    private static final String MESSAGE = "Resource not found!";
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;

    public ResourceNotFoundException(String resource) {
        super(MESSAGE, resource, HTTP_STATUS);
    }
}
