package com.autocardealer.inventory.tenant;

/**
 * Thread-local storage for the current tenant ID.
 * This context is used throughout the application to ensure tenant isolation.
 *
 * The tenant ID is set by the TenantFilter for each incoming request
 * and is automatically used by repositories to filter queries.
 */
public class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();

    /**
     * Sets the tenant ID for the current thread/request.
     * @param tenantId the tenant identifier
     */
    public static void setTenantId(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    /**
     * Gets the tenant ID for the current thread/request.
     * @return the tenant identifier, or null if not set
     */
    public static String getTenantId() {
        return CURRENT_TENANT.get();
    }

    /**
     * Clears the tenant ID from the current thread.
     * Should be called after request processing to prevent memory leaks.
     */
    public static void clear() {
        CURRENT_TENANT.remove();
    }

    /**
     * Checks if a tenant is currently set.
     * @return true if tenant ID is present, false otherwise
     */
    public static boolean hasTenant() {
        return CURRENT_TENANT.get() != null;
    }
}