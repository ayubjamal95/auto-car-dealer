# Multi-Tenant Inventory System

A production-grade, multi-tenant dealer and vehicle inventory management system built with Java 21, Spring Boot, and PostgreSQL.

## Features

- **Multi-Tenancy**: Automatic tenant isolation with header-based tenant identification
- **JWT Authentication**: Stateless authentication with role-based access control
- **Clean Architecture**: Controller → Service → Domain → Repository layers
- **REST APIs**: Comprehensive CRUD operations with filtering, pagination, and sorting
- **Type Safety**: Strong typing with DTOs and MapStruct mapping
- **Validation**: Bean validation with structured error responses
- **Auditing**: Automatic timestamps for creation and updates
- **OpenAPI**: Interactive API documentation with Swagger UI
- **Docker Support**: Complete containerization with Docker Compose
- **Testing**: Comprehensive unit and integration tests

## Tech Stack

- **Java**: 21
- **Spring Boot**: 3.2.5
- **Database**: PostgreSQL 16
- **Security**: JWT (JJWT 0.12.5)
- **Mapping**: MapStruct 1.5.5
- **Documentation**: SpringDoc OpenAPI
- **Build**: Maven
- **Testing**: JUnit 5, Mockito, AssertJ

## Architecture

```
com.example.inventory
├── config              # Configuration classes
├── controller          # REST controllers
├── service             # Business logic
├── domain              # Entities and enums
├── repository          # Data access layer
├── dto                 # Request/response DTOs
├── mapper              # MapStruct mappers
├── security            # JWT and security configuration
├── tenant              # Multi-tenancy infrastructure
├── exception           # Exception handling
└── validation          # Custom validators
```

## Quick Start

### Prerequisites

- Java 21+
- Maven 3.9+
- PostgreSQL 16+ (or use Docker)

### Running with Docker (Recommended)

```bash
# Start PostgreSQL and the application
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down
```

The application will be available at http://localhost:8080

### Running Locally

```bash
# 1. Start PostgreSQL
docker run -d \
  --name postgres \
  -e POSTGRES_DB=inventory_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:16-alpine

# 2. Build the application
mvn clean package

# 3. Run the application
java -jar target/inventory-1.0.0-SNAPSHOT.jar
```

### Running Tests

```bash
# Run all tests
mvn test

# Run with coverage
mvn test jacoco:report
```

## API Documentation

Once the application is running, access:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## Authentication

### Generating JWT Tokens

For development/testing, you can generate tokens manually or use a tool like:

```bash
# Example using online JWT generator
# Set payload:
{
  "sub": "testuser",
  "roles": "ROLE_USER",
  "iat": 1234567890,
  "exp": 9999999999
}
# Secret: your-secret-key-at-least-256-bits-long-for-hs256-algorithm-security
```

### Example Token (for testing)

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsInJvbGVzIjoiUk9MRV9VU0VSIn0.signature
```

For production, implement a proper authentication endpoint that validates credentials and issues tokens.

## API Usage Examples

### Required Headers

All requests require:

```
Authorization: Bearer <jwt-token>
X-Tenant-Id: tenant-123
Content-Type: application/json
```

### Create a Dealer

```bash
curl -X POST http://localhost:8080/dealers \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-Tenant-Id: tenant-123" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Premium Motors",
    "email": "contact@premiummotors.com",
    "subscriptionType": "PREMIUM"
  }'
```

### Get All Dealers (with pagination)

```bash
curl -X GET "http://localhost:8080/dealers?page=0&size=20&sort=name,asc" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-Tenant-Id: tenant-123"
```

### Get Dealer by ID

```bash
curl -X GET http://localhost:8080/dealers/{dealer-id} \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-Tenant-Id: tenant-123"
```

### Update Dealer

```bash
curl -X PATCH http://localhost:8080/dealers/{dealer-id} \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-Tenant-Id: tenant-123" \
  -H "Content-Type: application/json" \
  -d '{
    "subscriptionType": "PREMIUM"
  }'
```

### Delete Dealer

```bash
curl -X DELETE http://localhost:8080/dealers/{dealer-id} \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-Tenant-Id: tenant-123"
```

### Create a Vehicle

```bash
curl -X POST http://localhost:8080/vehicles \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-Tenant-Id: tenant-123" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "Toyota Camry 2024",
    "price": 28500.00,
    "status": "AVAILABLE",
    "dealerId": "dealer-uuid-here"
  }'
```

### Get Vehicles with Filters

```bash
# Filter by model, status, price range, and dealer subscription
curl -X GET "http://localhost:8080/vehicles?model=Toyota&status=AVAILABLE&priceMin=20000&priceMax=40000&subscription=PREMIUM&page=0&size=20" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-Tenant-Id: tenant-123"
```

### Admin: Count Dealers by Subscription (GLOBAL)

```bash
curl -X GET http://localhost:8080/admin/dealers/countBySubscription \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN" \
  -H "X-Tenant-Id: tenant-123
```

Note: Admin endpoints require `ROLE_GLOBAL_ADMIN` and return data across all tenants.

## Multi-Tenancy

### How It Works

1. **Tenant Header**: Every request must include `X-Tenant-Id` header
2. **Tenant Filter**: Extracts and validates tenant ID from header
3. **Tenant Context**: Stores tenant ID in thread-local storage
4. **Automatic Filtering**: All queries are automatically scoped to the current tenant
5. **Security**: Cross-tenant access attempts result in 403 Forbidden

### Tenant Isolation Levels

- **Data**: All entities include `tenantId` field
- **Repository**: All queries filter by tenant ID
- **Service**: Validates tenant ownership
- **API**: Returns 400 if tenant header is missing

### Cross-Tenant Access Protection

```java
// Attempting to access another tenant's data
GET /dealers/{id}
X-Tenant-Id: tenant-A

// If dealer belongs to tenant-B, returns:
// 404 Not Found (resource doesn't exist in your tenant)
```

## Security

### Roles

- **ROLE_USER**: Access to dealer and vehicle APIs within their tenant
- **ROLE_GLOBAL_ADMIN**: Access to admin APIs with cross-tenant data

### Security Best Practices

1. Always use HTTPS in production
2. Keep JWT secret key secure and rotate regularly
3. Use strong password hashing (BCrypt)
4. Implement rate limiting
5. Enable CORS with specific origins
6. Monitor and log security events
7. Implement proper session management

## Database Schema

### Dealers Table

```sql
CREATE TABLE dealers (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    subscription_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_dealer_tenant ON dealers(tenant_id);
CREATE INDEX idx_dealer_email ON dealers(email);
```

### Vehicles Table

```sql
CREATE TABLE vehicles (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    dealer_id UUID NOT NULL REFERENCES dealers(id),
    model VARCHAR(255) NOT NULL,
    price DECIMAL(19,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_vehicle_tenant ON vehicles(tenant_id);
CREATE INDEX idx_vehicle_dealer ON vehicles(dealer_id);
CREATE INDEX idx_vehicle_status ON vehicles(status);
CREATE INDEX idx_vehicle_model ON vehicles(model);
```

## Error Handling

All errors return a consistent format:

```json
{
  "timestamp": "2024-04-29T10:30:00",
  "status": 400,
  "message": "Validation failed",
  "path": "/dealers",
  "errors": [
    {
      "field": "email",
      "message": "Email must be valid"
    }
  ]
}
```

### HTTP Status Codes

- **200 OK**: Successful GET/PATCH
- **201 Created**: Successful POST
- **204 No Content**: Successful DELETE
- **400 Bad Request**: Validation errors, missing tenant
- **403 Forbidden**: Access denied, cross-tenant access
- **404 Not Found**: Resource not found
- **500 Internal Server Error**: Unexpected errors

## Configuration

Key configuration properties in `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/inventorydb
    username: postgres
    password: postgres

jwt:
  secret: your-secret-key-at-least-256-bits-long
  expiration: 86400000  # 24 hours

logging:
  level:
    com.example.inventory: DEBUG
```

### Environment Variables

Override configuration using environment variables:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://prod-db:5432/inventorydb
export SPRING_DATASOURCE_USERNAME=prod_user
export SPRING_DATASOURCE_PASSWORD=secure_password
export JWT_SECRET=your-production-secret-key
```


## Development

### Project Structure

```
auto-car-dealer/
├── src/
│   ├── main/
│   │   ├── java/com/example/inventory/
│   │   └── resources/
│   └── test/
│       ├── java/com/example/inventory/
│       └── resources/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

### Adding New Endpoints

1. Create DTO classes in `dto/`
2. Add mapping methods in `mapper/`
3. Implement business logic in `service/`
4. Create controller endpoints in `controller/`
5. Add unit tests in `src/test/`

### Code Style

- Use meaningful variable names
- Add JavaDoc comments for public methods
- Follow SOLID principles
- Keep methods small and focused
- Write tests for new features

## Troubleshooting

### Common Issues

**Problem**: Application fails to start
```
Solution: Ensure PostgreSQL is running and connection details are correct
```

**Problem**: 400 Bad Request on all endpoints
```
Solution: Check that X-Tenant-Id header is included in requests
```

**Problem**: 403 Forbidden
```
Solution: Verify JWT token includes correct role (ROLE_USER or ROLE_GLOBAL_ADMIN)
```

**Problem**: Cross-tenant data access
```
Solution: Verify all repository methods include tenant filtering
```

## Production Deployment

### Checklist

- [ ] Change default JWT secret
- [ ] Enable HTTPS/TLS
- [ ] Configure production database
- [ ] Set up monitoring and alerting
- [ ] Enable CORS for specific origins
- [ ] Implement rate limiting
- [ ] Set up log aggregation
- [ ] Configure backup strategy
- [ ] Review security headers
- [ ] Set appropriate log levels

### Performance Tuning

- Configure connection pool size based on load
- Add database indexes for frequently queried fields
- Enable database query caching
- Use pagination for large result sets
- Monitor and optimize slow queries

## License

MIT

## Support

For issues and questions:
- Open an issue on GitHub

## Contributors

Built by ayubjamal95