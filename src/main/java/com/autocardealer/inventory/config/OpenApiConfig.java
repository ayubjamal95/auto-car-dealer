package com.autocardealer.inventory.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for API documentation.
 *
 * Access the UI at: http://localhost:8080/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Multi-Tenant Inventory API")
                        .version("1.0.0")
                        .description("""
                                Production-grade multi-tenant dealer and vehicle inventory system.

                                ## Authentication
                                - All endpoints require JWT Bearer token in Authorization header
                                - Format: `Authorization: Bearer <token>`

                                ## Multi-Tenancy
                                - All requests must include X-Tenant-Id header
                                - Format: `X-Tenant-Id: tenant-123`
                                - Data is automatically isolated by tenant

                                ## Roles
                                - **USER**: Access to dealer and vehicle APIs within their tenant
                                - **GLOBAL_ADMIN**: Access to admin APIs with cross-tenant data
                                """)
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server")))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token")));
    }
}