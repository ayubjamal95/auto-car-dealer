package com.autocardealer.inventory.tenant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for TenantContext thread-local storage.
 */
class TenantContextTest {

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void setAndGetTenantId() {
        // Given
        String tenantId = "tenant-123";

        // When
        TenantContext.setTenantId(tenantId);

        // Then
        assertThat(TenantContext.getTenantId()).isEqualTo(tenantId);
        assertThat(TenantContext.hasTenant()).isTrue();
    }

    @Test
    void clearTenantId() {
        // Given
        TenantContext.setTenantId("tenant-123");

        // When
        TenantContext.clear();

        // Then
        assertThat(TenantContext.getTenantId()).isNull();
        assertThat(TenantContext.hasTenant()).isFalse();
    }

    @Test
    void hasTenant_WhenNotSet() {
        // When & Then
        assertThat(TenantContext.hasTenant()).isFalse();
    }

    @Test
    void threadLocalIsolation() throws InterruptedException {
        // Given
        String tenant1 = "tenant-1";
        String tenant2 = "tenant-2";

        // When
        TenantContext.setTenantId(tenant1);

        Thread thread = new Thread(() -> {
            TenantContext.setTenantId(tenant2);
            assertThat(TenantContext.getTenantId()).isEqualTo(tenant2);
            TenantContext.clear();
        });

        thread.start();
        thread.join();

        // Then - Main thread should still have tenant-1
        assertThat(TenantContext.getTenantId()).isEqualTo(tenant1);
    }
}