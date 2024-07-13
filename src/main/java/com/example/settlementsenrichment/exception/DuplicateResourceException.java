package com.example.settlementsenrichment.exception;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String resource, String identifier, String id) {
        super(resource + " already exists with " + identifier + ": " + id);
    }
}