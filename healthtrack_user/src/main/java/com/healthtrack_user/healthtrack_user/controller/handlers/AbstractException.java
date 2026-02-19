package com.healthtrack_user.healthtrack_user.controller.handlers;

import org.springframework.http.HttpStatus;

public class AbstractException extends RuntimeException{
    private final String resource;
    private final HttpStatus status;


    public AbstractException(String message, String resource, HttpStatus status) {
        super(message);
        this.resource = resource;
        this.status = status;
    }

    public String getResource() {
        return resource;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
