package com.autocardealer.inventory.service;

import com.autocardealer.inventory.domain.Dealer;
import com.autocardealer.inventory.dto.DealerRequest;
import com.autocardealer.inventory.dto.DealerResponse;
import com.autocardealer.inventory.dto.DealerUpdateRequest;
import com.autocardealer.inventory.dto.SubscriptionCountResponse;
import com.autocardealer.inventory.exception.ResourceNotFoundException;
import com.autocardealer.inventory.exception.TenantAccessViolationException;
import com.autocardealer.inventory.mapper.DealerMapper;
import com.autocardealer.inventory.repository.DealerRepository;
import com.autocardealer.inventory.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.autocardealer.inventory.domain.SubscriptionType.BASIC;
import static com.autocardealer.inventory.domain.SubscriptionType.PREMIUM;

/**
 * Service layer for Dealer operations.
 *
 * Handles:
 * - CRUD operations with automatic tenant isolation
 * - Validation of tenant access
 * - Business logic
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DealerService {

    private final DealerRepository dealerRepository;
    private final DealerMapper dealerMapper;

    /**
     * Creates a new dealer for the current tenant.
     *
     * @param request the dealer creation request
     * @return the created dealer
     */
    public DealerResponse createDealer(DealerRequest request) {
        String tenantId = getTenantId();
        log.info("Creating dealer for tenant: {}", tenantId);

        Dealer dealer = dealerMapper.toEntity(request);
        dealer.setTenantId(tenantId);

        Dealer savedDealer = dealerRepository.save(dealer);
        log.debug("Created dealer with id: {} for tenant: {}", savedDealer.getId(), tenantId);

        return dealerMapper.toResponse(savedDealer);
    }

    /**
     * Retrieves a dealer by ID within the current tenant.
     *
     * @param id the dealer ID
     * @return the dealer
     * @throws ResourceNotFoundException if dealer not found
     * @throws TenantAccessViolationException if dealer belongs to different tenant
     */
    @Transactional(readOnly = true)
    public DealerResponse getDealerById(UUID id) {
        String tenantId = getTenantId();
        log.debug("Fetching dealer {} for tenant: {}", id, tenantId);

        Dealer dealer = dealerRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer", id));

        return dealerMapper.toResponse(dealer);
    }

    /**
     * Retrieves all dealers for the current tenant with pagination.
     *
     * @param pageable pagination parameters
     * @return page of dealers
     */
    @Transactional(readOnly = true)
    public Page<DealerResponse> getAllDealers(Pageable pageable) {
        String tenantId = getTenantId();
        log.debug("Fetching dealers for tenant: {} with pagination: {}", tenantId, pageable);

        return dealerRepository.findByTenantId(tenantId, pageable)
                .map(dealerMapper::toResponse);
    }

    /**
     * Updates a dealer partially (PATCH operation).
     * Only non-null fields in the request will be updated.
     *
     * @param id the dealer ID
     * @param request the update request
     * @return the updated dealer
     * @throws ResourceNotFoundException if dealer not found
     */
    public DealerResponse updateDealer(UUID id, DealerUpdateRequest request) {
        String tenantId = getTenantId();
        log.info("Updating dealer {} for tenant: {}", id, tenantId);

        Dealer dealer = dealerRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Dealer", id));

        dealerMapper.updateEntityFromRequest(request, dealer);

        Dealer updatedDealer = dealerRepository.save(dealer);
        log.debug("Updated dealer: {}", updatedDealer.getId());

        return dealerMapper.toResponse(updatedDealer);
    }

    /**
     * Deletes a dealer by ID within the current tenant.
     *
     * @param id the dealer ID
     * @throws ResourceNotFoundException if dealer not found
     */
    public void deleteDealer(UUID id) {
        String tenantId = getTenantId();
        log.info("Deleting dealer {} for tenant: {}", id, tenantId);

        if (!dealerRepository.existsByIdAndTenantId(id, tenantId)) {
            throw new ResourceNotFoundException("Dealer", id);
        }

        dealerRepository.deleteByIdAndTenantId(id, tenantId);
        log.debug("Deleted dealer: {}", id);
    }

    /**
     * Counts dealers by subscription type across ALL tenants.
     * This is a GLOBAL admin-only operation.
     *
     * @return subscription counts
     */
    @Transactional(readOnly = true)
    public SubscriptionCountResponse countBySubscriptionType() {
        log.info("Fetching global dealer counts by subscription type");

        Long basicCount = dealerRepository.countBySubscriptionType(BASIC);
        Long premiumCount = dealerRepository.countBySubscriptionType(PREMIUM);

        return SubscriptionCountResponse.builder()
                .basic(basicCount)
                .premium(premiumCount)
                .build();
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