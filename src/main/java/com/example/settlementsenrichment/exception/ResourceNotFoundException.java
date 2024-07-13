package com.example.settlementsenrichment.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String id) {
        super("Resource not found with ID: " + id);
    }

    public ResourceNotFoundException(String message, String id) {
        super(message + ": " + id);
    }
}