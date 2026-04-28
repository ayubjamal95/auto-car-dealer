package com.autocardealer.inventory.controller;

import com.autocardealer.inventory.domain.SubscriptionType;
import com.autocardealer.inventory.domain.VehicleStatus;
import com.autocardealer.inventory.dto.VehicleRequest;
import com.autocardealer.inventory.dto.VehicleResponse;
import com.autocardealer.inventory.dto.VehicleUpdateRequest;
import com.autocardealer.inventory.service.VehicleService;
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

import java.math.BigDecimal;
import java.util.UUID;

/**
 * REST controller for Vehicle operations.
 *
 * All endpoints require:
 * - Authentication (JWT token)
 * - X-Tenant-Id header
 */
@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Vehicles", description = "Vehicle inventory management APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class VehicleController {

    private final VehicleService vehicleService;

    /**
     * Creates a new vehicle.
     *
     * POST /vehicles
     */
    @PostMapping
    @Operation(summary = "Create a new vehicle", description = "Creates a vehicle for the current tenant")
    public ResponseEntity<VehicleResponse> createVehicle(@Valid @RequestBody VehicleRequest request) {
        log.info("Received request to create vehicle");
        VehicleResponse response = vehicleService.createVehicle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves a vehicle by ID.
     *
     * GET /vehicles/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get vehicle by ID", description = "Retrieves a vehicle within the current tenant")
    public ResponseEntity<VehicleResponse> getVehicleById(
            @Parameter(description = "Vehicle ID") @PathVariable UUID id
    ) {
        log.info("Received request to get vehicle: {}", id);
        VehicleResponse response = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves vehicles with optional filtering.
     *
     * GET /vehicles?model=Toyota&status=AVAILABLE&priceMin=10000&priceMax=50000&subscription=PREMIUM
     */
    @GetMapping
    @Operation(
            summary = "Get all vehicles",
            description = "Retrieves vehicles with optional filtering by model, status, price range, and dealer subscription"
    )
    public ResponseEntity<Page<VehicleResponse>> getVehicles(
            @Parameter(description = "Filter by model (partial match)") @RequestParam(required = false) String model,
            @Parameter(description = "Filter by status") @RequestParam(required = false) VehicleStatus status,
            @Parameter(description = "Minimum price (inclusive)") @RequestParam(required = false) BigDecimal priceMin,
            @Parameter(description = "Maximum price (inclusive)") @RequestParam(required = false) BigDecimal priceMax,
            @Parameter(description = "Filter by dealer subscription type") @RequestParam(required = false) SubscriptionType subscription,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        log.info("Received request to get vehicles with filters - model: {}, status: {}, priceMin: {}, priceMax: {}, subscription: {}",
                model, status, priceMin, priceMax, subscription);

        Page<VehicleResponse> response = vehicleService.getVehicles(
                model, status, priceMin, priceMax, subscription, pageable
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Updates a vehicle partially.
     *
     * PATCH /vehicles/{id}
     */
    @PatchMapping("/{id}")
    @Operation(summary = "Update vehicle", description = "Partially updates a vehicle (only provided fields are updated)")
    public ResponseEntity<VehicleResponse> updateVehicle(
            @Parameter(description = "Vehicle ID") @PathVariable UUID id,
            @Valid @RequestBody VehicleUpdateRequest request
    ) {
        log.info("Received request to update vehicle: {}", id);
        VehicleResponse response = vehicleService.updateVehicle(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a vehicle.
     *
     * DELETE /vehicles/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete vehicle", description = "Deletes a vehicle within the current tenant")
    public ResponseEntity<Void> deleteVehicle(
            @Parameter(description = "Vehicle ID") @PathVariable UUID id
    ) {
        log.info("Received request to delete vehicle: {}", id);
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}