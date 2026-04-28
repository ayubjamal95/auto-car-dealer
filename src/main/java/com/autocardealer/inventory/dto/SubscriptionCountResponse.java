package com.autocardealer.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for dealer count by subscription type.
 * Used by the admin endpoint to provide statistics.
 *
 * Note: This provides GLOBAL counts across all tenants (admin-only feature).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionCountResponse {

    private Long basic;
    private Long premium;
}