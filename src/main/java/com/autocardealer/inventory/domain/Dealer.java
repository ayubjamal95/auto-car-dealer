package com.autocardealer.inventory.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a dealer in the system.
 * Dealers belong to a tenant and can have multiple vehicles.
 */
@Entity
@Table(
    name = "dealers",
    indexes = {
        @Index(name = "idx_dealer_tenant", columnList = "tenant_id"),
        @Index(name = "idx_dealer_email", columnList = "email")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dealer extends BaseEntity {

    /**
     * Name of the dealer.
     */
    @NotBlank(message = "Dealer name is required")
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Email address of the dealer.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(name = "email", nullable = false)
    private String email;

    /**
     * Subscription tier of the dealer (BASIC or PREMIUM).
     */
    @NotNull(message = "Subscription type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_type", nullable = false)
    private SubscriptionType subscriptionType;

    /**
     * Vehicles owned by this dealer.
     */
    @OneToMany(mappedBy = "dealer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Vehicle> vehicles = new ArrayList<>();
}