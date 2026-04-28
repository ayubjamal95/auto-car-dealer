package com.autocardealer.inventory.dto;

import com.autocardealer.inventory.domain.VehicleStatus;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for partially updating a vehicle (PATCH operation).
 * All fields are optional.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleUpdateRequest {

    private String model;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    private VehicleStatus status;
}