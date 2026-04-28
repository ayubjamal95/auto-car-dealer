package com.autocardealer.inventory.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entity representing a vehicle in the inventory.
 * Each vehicle belongs to a dealer and a tenant.
 */
@Entity
@Table(
    name = "vehicles",
    indexes = {
        @Index(name = "idx_vehicle_tenant", columnList = "tenant_id"),
        @Index(name = "idx_vehicle_dealer", columnList = "dealer_id"),
        @Index(name = "idx_vehicle_status", columnList = "status"),
        @Index(name = "idx_vehicle_model", columnList = "model")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle extends BaseEntity {

    /**
     * Vehicle model name.
     */
    @NotBlank(message = "Model is required")
    @Column(name = "model", nullable = false)
    private String model;

    /**
     * Price of the vehicle.
     */
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    /**
     * Current status of the vehicle (AVAILABLE or SOLD).
     */
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VehicleStatus status;

    /**
     * Reference to the dealer who owns this vehicle.
     */
    @NotNull(message = "Dealer is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dealer_id", nullable = false)
    private Dealer dealer;

    /**
     * Helper method to get dealer ID without triggering lazy loading.
     */
    public UUID getDealerId() {
        return dealer != null ? dealer.getId() : null;
    }
}