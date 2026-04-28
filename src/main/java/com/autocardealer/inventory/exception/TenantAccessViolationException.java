package com.autocardealer.inventory.exception;

/**
 * Exception thrown when a user attempts to access resources from a different tenant.
 * Results in a 403 Forbidden response.
 */
public class TenantAccessViolationException extends RuntimeException {

    public TenantAccessViolationException(String message) {
        super(message);
    }

    public TenantAccessViolationException() {
        super("Access denied: Cross-tenant access is not allowed");
    }
}