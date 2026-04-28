package com.autocardealer.inventory.exception;

/**
 * Exception thrown when a request is missing the required X-Tenant-Id header.
 * Results in a 400 Bad Request response.
 */
public class MissingTenantException extends RuntimeException {

    public MissingTenantException(String message) {
        super(message);
    }
}