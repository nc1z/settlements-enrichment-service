package com.example.settlementsenrichment.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, String identifier, String id) {
        super(resource + " not found with " + identifier + ": " + id);
    }
}