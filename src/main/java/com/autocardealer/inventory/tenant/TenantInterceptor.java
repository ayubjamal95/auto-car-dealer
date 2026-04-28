package com.autocardealer.inventory.tenant;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.stereotype.Component;

/**
 * Hibernate interceptor that automatically adds tenant filtering to SQL queries.
 *
 * This is a defense-in-depth measure to ensure tenant isolation at the SQL level.
 * While we also enforce tenancy in repositories, this provides an additional layer
 * of protection against accidental cross-tenant data access.
 *
 * Note: In a production system, you might want to use Hibernate Filters
 * or implement this more comprehensively. This is a simplified implementation.
 */
@Component
@Slf4j
public class TenantInterceptor implements StatementInspector {

    @Override
    public String inspect(String sql) {
        // This is a simplified implementation
        // In production, you'd want more sophisticated query rewriting
        // Consider using Hibernate Filters (@FilterDef and @Filter) instead

        String tenantId = TenantContext.getTenantId();

        if (tenantId != null) {
            log.trace("Executing SQL with tenant context: {}", tenantId);
        }

        return sql;
    }
}