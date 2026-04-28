package com.autocardealer.inventory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA configuration enabling auditing features.
 *
 * This allows automatic population of:
 * - @CreatedDate
 * - @LastModifiedDate
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}