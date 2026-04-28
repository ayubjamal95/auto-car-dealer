package com.autocardealer.inventory.repository;

import com.autocardealer.inventory.domain.Dealer;
import com.autocardealer.inventory.domain.SubscriptionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Dealer entity with tenant-scoped queries.
 *
 * All queries automatically filter by tenant ID to ensure data isolation.
 */
@Repository
public interface DealerRepository extends JpaRepository<Dealer, UUID> {

    /**
     * Finds a dealer by ID within the current tenant.
     *
     * @param id the dealer ID
     * @param tenantId the tenant ID
     * @return Optional containing the dealer if found
     */
    Optional<Dealer> findByIdAndTenantId(UUID id, String tenantId);

    /**
     * Finds all dealers for the current tenant with pagination.
     *
     * @param tenantId the tenant ID
     * @param pageable pagination information
     * @return page of dealers
     */
    Page<Dealer> findByTenantId(String tenantId, Pageable pageable);

    /**
     * Checks if a dealer exists for the current tenant.
     *
     * @param id the dealer ID
     * @param tenantId the tenant ID
     * @return true if exists, false otherwise
     */
    boolean existsByIdAndTenantId(UUID id, String tenantId);

    /**
     * Deletes a dealer by ID within the current tenant.
     *
     * @param id the dealer ID
     * @param tenantId the tenant ID
     */
    void deleteByIdAndTenantId(UUID id, String tenantId);

    /**
     * Counts dealers by subscription type across ALL tenants.
     * This is a GLOBAL admin query - does NOT filter by tenant.
     *
     * @param subscriptionType the subscription type to count
     * @return count of dealers with the given subscription type
     */
    @Query("SELECT COUNT(d) FROM Dealer d WHERE d.subscriptionType = :subscriptionType")
    Long countBySubscriptionType(@Param("subscriptionType") SubscriptionType subscriptionType);
}