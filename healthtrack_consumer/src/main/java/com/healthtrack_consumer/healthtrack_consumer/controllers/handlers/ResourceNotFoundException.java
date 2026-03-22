package com.healthtrack_consumer.healthtrack_consumer.controllers.handlers;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
