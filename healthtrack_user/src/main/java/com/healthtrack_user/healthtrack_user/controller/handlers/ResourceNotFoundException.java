package com.healthtrack_user.healthtrack_user.controller.handlers;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends AbstractException {
    private static final String MESSAGE = "Resource not found!";
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;

    public ResourceNotFoundException(String resource) {
        super(MESSAGE, resource, HTTP_STATUS);
    }
}
