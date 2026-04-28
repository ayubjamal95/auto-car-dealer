package com.autocardealer.inventory.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base entity class providing common fields for all entities.
 * Includes:
 * - UUID as primary key
 * - Tenant ID for multi-tenancy
 * - Audit fields (createdAt, updatedAt)
 */
@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /**
     * Tenant identifier for multi-tenancy isolation.
     * Every entity must belong to a tenant.
     */
    @Column(name = "tenant_id", nullable = false, updatable = false)
    private String tenantId;

    /**
     * Timestamp when the entity was created.
     */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the entity was last updated.
     */
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}