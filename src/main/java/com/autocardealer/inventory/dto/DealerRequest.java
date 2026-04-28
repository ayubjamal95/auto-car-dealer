package com.autocardealer.inventory.dto;

import com.autocardealer.inventory.domain.SubscriptionType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating or updating a dealer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealerRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotNull(message = "Subscription type is required")
    private SubscriptionType subscriptionType;
}