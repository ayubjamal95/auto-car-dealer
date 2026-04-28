package com.autocardealer.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for the Multi-Tenant Inventory System.
 *
 * This application provides:
 * - Multi-tenant dealer and vehicle inventory management
 * - JWT-based authentication with role-based access control
 * - Automatic tenant isolation for all data operations
 * - RESTful APIs with comprehensive filtering and pagination
 * - OpenAPI/Swagger documentation
 *
 * Key Features:
 * - Clean Architecture (Controller → Service → Domain → Repository)
 * - SOLID principles
 * - DTO pattern with MapStruct
 * - Global exception handling
 * - Bean validation
 * - Audit trails (createdAt, updatedAt)
 *
 * Access Points:
 * - API: http://localhost:8080
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - API Docs: http://localhost:8080/api-docs
 */
@SpringBootApplication
public class InventoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryApplication.class, args);
    }
}