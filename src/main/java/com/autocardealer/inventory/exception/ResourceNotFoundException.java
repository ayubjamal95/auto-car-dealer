package com.autocardealer.inventory.exception;

import java.util.UUID;

/**
 * Exception thrown when a requested resource is not found.
 * Results in a 404 Not Found response.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceType, UUID id) {
        super(String.format("%s with id '%s' not found", resourceType, id));
    }
}