package com.autocardealer.inventory.dto;

import com.autocardealer.inventory.domain.SubscriptionType;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for partially updating a dealer (PATCH operation).
 * All fields are optional.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DealerUpdateRequest {

    private String name;

    @Email(message = "Email must be valid")
    private String email;

    private SubscriptionType subscriptionType;
}