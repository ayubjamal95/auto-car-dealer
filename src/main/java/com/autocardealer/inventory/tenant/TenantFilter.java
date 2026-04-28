package com.autocardealer.inventory.tenant;

import com.autocardealer.inventory.exception.MissingTenantException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that extracts and validates the tenant ID from the X-Tenant-Id header.
 *
 * This filter:
 * 1. Extracts X-Tenant-Id header from each request
 * 2. Validates that the tenant ID is present (except for public endpoints)
 * 3. Sets the tenant ID in TenantContext for use throughout the request
 * 4. Clears the context after request processing
 *
 * Missing tenant header results in a 400 Bad Request response.
 */
@Component
@Slf4j
public class TenantFilter extends OncePerRequestFilter {

    public static final String TENANT_HEADER = "X-Tenant-Id";

    /**
     * Paths that don't require tenant validation.
     * Includes:
     * - Health checks and monitoring endpoints
     * - API documentation
     * - Admin endpoints (they are global and don't need tenant context)
     */
    private static final String[] PUBLIC_PATHS = {
        "/actuator",
        "/swagger-ui",
        "/api-docs",
        "/v3/api-docs",
        "/admin"  // Admin endpoints are global and don't require tenant
    };

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        // Skip tenant validation for public paths
        if (isPublicPath(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String tenantId = request.getHeader(TENANT_HEADER);

            log.debug("Processing request for path: {} with tenant: {}", requestPath, tenantId);

            if (tenantId == null || tenantId.trim().isEmpty()) {
                log.warn("Missing tenant header for path: {}", requestPath);
                throw new MissingTenantException("Tenant ID is required. Please provide X-Tenant-Id header.");
            }

            // Set tenant in context for the duration of this request
            TenantContext.setTenantId(tenantId);

            filterChain.doFilter(request, response);

        } finally {
            // Always clear tenant context to prevent memory leaks and cross-request contamination
            TenantContext.clear();
        }
    }

    /**
     * Checks if the given path is a public path that doesn't require tenant validation.
     */
    private boolean isPublicPath(String path) {
        for (String publicPath : PUBLIC_PATHS) {
            if (path.startsWith(publicPath)) {
                return true;
            }
        }
        return false;
    }
}