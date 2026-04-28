package com.autocardealer.inventory.repository;

import com.autocardealer.inventory.domain.Dealer;
import com.autocardealer.inventory.domain.SubscriptionType;
import com.autocardealer.inventory.domain.Vehicle;
import com.autocardealer.inventory.domain.VehicleStatus;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

/**
 * JPA Specifications for building complex Vehicle queries.
 *
 * All specifications automatically include tenant filtering to ensure data isolation.
 */
public class VehicleSpecification {

    /**
     * Creates a specification that filters vehicles by tenant ID.
     * This should be applied to ALL queries.
     */
    public static Specification<Vehicle> hasTenantId(String tenantId) {
        return (root, query, cb) -> cb.equal(root.get("tenantId"), tenantId);
    }

    /**
     * Filters by vehicle model (case-insensitive partial match).
     */
    public static Specification<Vehicle> hasModel(String model) {
        return (root, query, cb) -> {
            if (model == null || model.isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("model")), "%" + model.toLowerCase() + "%");
        };
    }

    /**
     * Filters by vehicle status.
     */
    public static Specification<Vehicle> hasStatus(VehicleStatus status) {
        return (root, query, cb) -> {
            if (status == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("status"), status);
        };
    }

    /**
     * Filters by minimum price (inclusive).
     */
    public static Specification<Vehicle> hasPriceGreaterThanOrEqual(BigDecimal minPrice) {
        return (root, query, cb) -> {
            if (minPrice == null) {
                return cb.conjunction();
            }
            return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
        };
    }

    /**
     * Filters by maximum price (inclusive).
     */
    public static Specification<Vehicle> hasPriceLessThanOrEqual(BigDecimal maxPrice) {
        return (root, query, cb) -> {
            if (maxPrice == null) {
                return cb.conjunction();
            }
            return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
        };
    }

    /**
     * Filters by dealer subscription type.
     * Joins with Dealer table to check subscription.
     */
    public static Specification<Vehicle> hasDealerSubscription(SubscriptionType subscriptionType) {
        return (root, query, cb) -> {
            if (subscriptionType == null) {
                return cb.conjunction();
            }
            Join<Vehicle, Dealer> dealerJoin = root.join("dealer");
            return cb.equal(dealerJoin.get("subscriptionType"), subscriptionType);
        };
    }

    /**
     * Builds a combined specification with all provided filters.
     * Automatically includes tenant filtering.
     *
     * @param tenantId the tenant ID (required)
     * @param model optional model filter
     * @param status optional status filter
     * @param minPrice optional minimum price
     * @param maxPrice optional maximum price
     * @param subscriptionType optional dealer subscription filter
     * @return combined specification
     */
    public static Specification<Vehicle> buildSpecification(
            String tenantId,
            String model,
            VehicleStatus status,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            SubscriptionType subscriptionType
    ) {
        return Specification
                .where(hasTenantId(tenantId))
                .and(hasModel(model))
                .and(hasStatus(status))
                .and(hasPriceGreaterThanOrEqual(minPrice))
                .and(hasPriceLessThanOrEqual(maxPrice))
                .and(hasDealerSubscription(subscriptionType));
    }
}