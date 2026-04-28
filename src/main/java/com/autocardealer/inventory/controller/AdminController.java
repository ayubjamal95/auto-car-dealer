package com.autocardealer.inventory.controller;

import com.autocardealer.inventory.dto.SubscriptionCountResponse;
import com.autocardealer.inventory.service.DealerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for Admin operations.
 *
 * All endpoints require:
 * - Authentication (JWT token)
 * - GLOBAL_ADMIN role
 *
 * Note: Admin endpoints provide GLOBAL data across all tenants.
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin", description = "Admin APIs (GLOBAL_ADMIN only)")
@SecurityRequirement(name = "Bearer Authentication")
public class AdminController {

    private final DealerService dealerService;

    /**
     * Gets dealer count by subscription type across ALL tenants.
     *
     * This is a GLOBAL query - returns counts across all tenants.
     * Only accessible by users with GLOBAL_ADMIN role.
     *
     * GET /admin/dealers/countBySubscription
     *
     * Response format:
     * {
     *   "basic": 150,
     *   "premium": 75
     * }
     */
    @GetMapping("/dealers/countBySubscription")
    @PreAuthorize("hasRole('GLOBAL_ADMIN')")
    @Operation(
            summary = "Count dealers by subscription type",
            description = "Returns GLOBAL dealer counts by subscription type across all tenants. Requires GLOBAL_ADMIN role."
    )
    public ResponseEntity<SubscriptionCountResponse> countBySubscription() {
        log.info("Admin request: counting dealers by subscription type (GLOBAL)");
        SubscriptionCountResponse response = dealerService.countBySubscriptionType();
        return ResponseEntity.ok(response);
    }
}