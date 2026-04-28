package com.autocardealer.inventory.repository;

import com.autocardealer.inventory.domain.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Vehicle entity with tenant-scoped queries.
 *
 * Implements JpaSpecificationExecutor to support complex filtering queries
 * while maintaining tenant isolation.
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID>, JpaSpecificationExecutor<Vehicle> {

    /**
     * Finds a vehicle by ID within the current tenant.
     *
     * @param id the vehicle ID
     * @param tenantId the tenant ID
     * @return Optional containing the vehicle if found
     */
    Optional<Vehicle> findByIdAndTenantId(UUID id, String tenantId);

    /**
     * Finds all vehicles for the current tenant with pagination.
     *
     * @param tenantId the tenant ID
     * @param pageable pagination information
     * @return page of vehicles
     */
    Page<Vehicle> findByTenantId(String tenantId, Pageable pageable);

    /**
     * Checks if a vehicle exists for the current tenant.
     *
     * @param id the vehicle ID
     * @param tenantId the tenant ID
     * @return true if exists, false otherwise
     */
    boolean existsByIdAndTenantId(UUID id, String tenantId);

    /**
     * Deletes a vehicle by ID within the current tenant.
     *
     * @param id the vehicle ID
     * @param tenantId the tenant ID
     */
    void deleteByIdAndTenantId(UUID id, String tenantId);
}