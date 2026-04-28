package com.autocardealer.inventory.dto;

import com.autocardealer.inventory.domain.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for vehicle information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponse {

    private UUID id;
    private String tenantId;
    private String model;
    private BigDecimal price;
    private VehicleStatus status;
    private UUID dealerId;
    private String dealerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}