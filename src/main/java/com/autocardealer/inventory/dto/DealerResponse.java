package com.autocardealer.inventory.dto;

import com.autocardealer.inventory.domain.SubscriptionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for dealer information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealerResponse {

    private UUID id;
    private String tenantId;
    private String name;
    private String email;
    private SubscriptionType subscriptionType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}