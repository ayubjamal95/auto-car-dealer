package com.autocardealer.inventory.service;

import com.autocardealer.inventory.domain.Dealer;
import com.autocardealer.inventory.domain.SubscriptionType;
import com.autocardealer.inventory.domain.Vehicle;
import com.autocardealer.inventory.domain.VehicleStatus;
import com.autocardealer.inventory.dto.VehicleRequest;
import com.autocardealer.inventory.dto.VehicleResponse;
import com.autocardealer.inventory.dto.VehicleUpdateRequest;
import com.autocardealer.inventory.exception.ResourceNotFoundException;
import com.autocardealer.inventory.mapper.VehicleMapper;
import com.autocardealer.inventory.repository.DealerRepository;
import com.autocardealer.inventory.repository.VehicleRepository;
import com.autocardealer.inventory.repository.VehicleSpecification;
import com.autocardealer.inventory.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Service layer for Vehicle operations.
 *
 * Handles:
 * - CRUD operations with automatic tenant isolation
 * - Complex filtering (model, status, price range, subscription)
 * - Validation of dealer ownership within tenant
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final DealerRepository dealerRepository;
    private final VehicleMapper vehicleMapper;

    /**
     * Creates a new vehicle for the current tenant.
     * Validates that the dealer belongs to the same tenant.
     *
     * @param request the vehicle creation request
     * @return the created vehicle
     * @throws ResourceNotFoundException if dealer not found or doesn't belong to tenant
     */
    public VehicleResponse createVehicle(VehicleRequest request) {
        String tenantId = getTenantId();
        log.info("Creating vehicle for tenant: {}", tenantId);

        // Validate dealer exists and belongs to same tenant
        Dealer dealer = dealerRepository.findByIdAndTenantId(request.getDealerId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Dealer with id '" + request.getDealerId() + "' not found or doesn't belong to your tenant"
                ));

        Vehicle vehicle = vehicleMapper.toEntity(request);
        vehicle.setTenantId(tenantId);
        vehicle.setDealer(dealer);

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        log.debug("Created vehicle with id: {} for tenant: {}", savedVehicle.getId(), tenantId);

        return vehicleMapper.toResponse(savedVehicle);
    }

    /**
     * Retrieves a vehicle by ID within the current tenant.
     *
     * @param id the vehicle ID
     * @return the vehicle
     * @throws ResourceNotFoundException if vehicle not found
     */
    @Transactional(readOnly = true)
    public VehicleResponse getVehicleById(UUID id) {
        String tenantId = getTenantId();
        log.debug("Fetching vehicle {} for tenant: {}", id, tenantId);

        Vehicle vehicle = vehicleRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", id));

        return vehicleMapper.toResponse(vehicle);
    }

    /**
     * Retrieves vehicles with optional filtering.
     *
     * Supports filtering by:
     * - model (partial match, case-insensitive)
     * - status
     * - price range (min/max)
     * - dealer subscription type
     *
     * All results are automatically scoped to the current tenant.
     *
     * @param model optional model filter
     * @param status optional status filter
     * @param priceMin optional minimum price
     * @param priceMax optional maximum price
     * @param subscription optional dealer subscription filter
     * @param pageable pagination parameters
     * @return page of vehicles
     */
    @Transactional(readOnly = true)
    public Page<VehicleResponse> getVehicles(
            String model,
            VehicleStatus status,
            BigDecimal priceMin,
            BigDecimal priceMax,
            SubscriptionType subscription,
            Pageable pageable
    ) {
        String tenantId = getTenantId();
        log.debug("Fetching vehicles for tenant: {} with filters - model: {}, status: {}, " +
                        "priceMin: {}, priceMax: {}, subscription: {}",
                tenantId, model, status, priceMin, priceMax, subscription);

        Specification<Vehicle> spec = VehicleSpecification.buildSpecification(
                tenantId, model, status, priceMin, priceMax, subscription
        );

        return vehicleRepository.findAll(spec, pageable)
                .map(vehicleMapper::toResponse);
    }

    /**
     * Updates a vehicle partially (PATCH operation).
     * Only non-null fields in the request will be updated.
     *
     * @param id the vehicle ID
     * @param request the update request
     * @return the updated vehicle
     * @throws ResourceNotFoundException if vehicle not found
     */
    public VehicleResponse updateVehicle(UUID id, VehicleUpdateRequest request) {
        String tenantId = getTenantId();
        log.info("Updating vehicle {} for tenant: {}", id, tenantId);

        Vehicle vehicle = vehicleRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", id));

        vehicleMapper.updateEntityFromRequest(request, vehicle);

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        log.debug("Updated vehicle: {}", updatedVehicle.getId());

        return vehicleMapper.toResponse(updatedVehicle);
    }

    /**
     * Deletes a vehicle by ID within the current tenant.
     *
     * @param id the vehicle ID
     * @throws ResourceNotFoundException if vehicle not found
     */
    public void deleteVehicle(UUID id) {
        String tenantId = getTenantId();
        log.info("Deleting vehicle {} for tenant: {}", id, tenantId);

        if (!vehicleRepository.existsByIdAndTenantId(id, tenantId)) {
            throw new ResourceNotFoundException("Vehicle", id);
        }

        vehicleRepository.deleteByIdAndTenantId(id, tenantId);
        log.debug("Deleted vehicle: {}", id);
    }

    /**
     * Gets the current tenant ID from context.
     *
     * @return tenant ID
     * @throws IllegalStateException if tenant context is not set
     */
    private String getTenantId() {
        String tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context not set");
        }
        return tenantId;
    }
}