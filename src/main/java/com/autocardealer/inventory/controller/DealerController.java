package com.autocardealer.inventory.controller;

import com.autocardealer.inventory.dto.DealerRequest;
import com.autocardealer.inventory.dto.DealerResponse;
import com.autocardealer.inventory.dto.DealerUpdateRequest;
import com.autocardealer.inventory.service.DealerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for Dealer operations.
 *
 * All endpoints require:
 * - Authentication (JWT token)
 * - X-Tenant-Id header
 */
@RestController
@RequestMapping("/dealers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Dealers", description = "Dealer management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class DealerController {

    private final DealerService dealerService;

    /**
     * Creates a new dealer.
     *
     * POST /dealers
     */
    @PostMapping
    @Operation(summary = "Create a new dealer", description = "Creates a dealer for the current tenant")
    public ResponseEntity<DealerResponse> createDealer(@Valid @RequestBody DealerRequest request) {
        log.info("Received request to create dealer");
        DealerResponse response = dealerService.createDealer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves a dealer by ID.
     *
     * GET /dealers/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get dealer by ID", description = "Retrieves a dealer within the current tenant")
    public ResponseEntity<DealerResponse> getDealerById(
            @Parameter(description = "Dealer ID") @PathVariable UUID id
    ) {
        log.info("Received request to get dealer: {}", id);
        DealerResponse response = dealerService.getDealerById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all dealers with pagination and sorting.
     *
     * GET /dealers?page=0&size=20&sort=name,asc
     */
    @GetMapping
    @Operation(summary = "Get all dealers", description = "Retrieves all dealers for the current tenant with pagination")
    public ResponseEntity<Page<DealerResponse>> getAllDealers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        log.info("Received request to get all dealers with pagination: {}", pageable);
        Page<DealerResponse> response = dealerService.getAllDealers(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates a dealer partially.
     *
     * PATCH /dealers/{id}
     */
    @PatchMapping("/{id}")
    @Operation(summary = "Update dealer", description = "Partially updates a dealer (only provided fields are updated)")
    public ResponseEntity<DealerResponse> updateDealer(
            @Parameter(description = "Dealer ID") @PathVariable UUID id,
            @Valid @RequestBody DealerUpdateRequest request
    ) {
        log.info("Received request to update dealer: {}", id);
        DealerResponse response = dealerService.updateDealer(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a dealer.
     *
     * DELETE /dealers/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete dealer", description = "Deletes a dealer within the current tenant")
    public ResponseEntity<Void> deleteDealer(
            @Parameter(description = "Dealer ID") @PathVariable UUID id
    ) {
        log.info("Received request to delete dealer: {}", id);
        dealerService.deleteDealer(id);
        return ResponseEntity.noContent().build();
    }
}