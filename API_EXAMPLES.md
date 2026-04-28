# API Examples

Complete examples for testing the Multi-Tenant Inventory API.

## Postman Collection

Import these examples into Postman for easy testing.

## Setup

### 1. Generate JWT Token (for testing)

For development, you can use this sample token:

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0dXNlciIsInJvbGVzIjoiUk9MRV9VU0VSIiwiaWF0IjoxNjE2MjM5MDIyLCJleHAiOjk5OTk5OTk5OTl9.signature
```

**Note**: In production, implement a login endpoint that validates credentials and returns a real JWT token.

### 2. Set Environment Variables (Postman/Insomnia)

```
BASE_URL=http://localhost:8080
JWT_TOKEN=your-jwt-token-here
TENANT_ID=tenant-123
```

---

## Dealer APIs

### 1. Create Dealer

**POST** `/dealers`

```bash
curl -X POST http://localhost:8080/dealers \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-Tenant-Id: tenant-123" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Premium Auto Group",
    "email": "info@premiumauto.com",
    "subscriptionType": "PREMIUM"
  }'
```

**Response** (201 Created):
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "tenantId": "tenant-123",
  "name": "Premium Auto Group",
  "email": "info@premiumauto.com",
  "subscriptionType": "PREMIUM",
  "createdAt": "2024-04-29T10:30:00",
  "updatedAt": "2024-04-29T10:30:00"
}
```

### 2. Get Dealer by ID

**GET** `/dealers/{id}`

```bash
curl -X GET http://localhost:8080/dealers/550e8400-e29b-41d4-a716-446655440000 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-Tenant-Id: tenant-123"
```

**Response** (200 OK):
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "tenantId": "tenant-123",
  "name": "Premium Auto Group",
  "email": "info@premiumauto.com",
  "subscriptionType": "PREMIUM",
  "createdAt": "2024-04-29T10:30:00",
  "updatedAt": "2024-04-29T10:30:00"
}
```

### 3. Get All Dealers (Paginated)

**GET** `/dealers?page=0&size=20&sort=name,asc`

```bash
curl -X GET "http://localhost:8080/dealers?page=0&size=20&sort=name,asc" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-Tenant-Id: tenant-123"
```

**Response** (200 OK):
```json
{
  "content": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "tenantId": "tenant-123",
      "name": "Premium Auto Group",
      "email": "info@premiumauto.com",
      "subscriptionType": "PREMIUM",
      "createdAt": "2024-04-29T10:30:00",
      "updatedAt": "2024-04-29T10:30:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    }
  },
  "totalPages": 1,
  "totalElements": 1,
  "last": true,
  "first": true,
  "size": 20,
  "number": 0,
  "numberOfElements": 1,
  "empty": false
}
```

### 4. Update Dealer (Partial)

**PATCH** `/dealers/{id}`

```bash
curl -X PATCH http://localhost:8080/dealers/550e8400-e29b-41d4-a716-446655440000 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-Tenant-Id: tenant-123" \
  -H "Content-Type: application/json" \
  -d '{
    "subscriptionType": "BASIC",
    "email": "newemail@premiumauto.com"
  }'
```

**Response** (200 OK):
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "tenantId": "tenant-123",
  "name": "Premium Auto Group",
  "email": "newemail@premiumauto.com",
  "subscriptionType": "BASIC",
  "createdAt": "2024-04-29T10:30:00",
  "updatedAt": "2024-04-29T11:15:00"
}
```

### 5. Delete Dealer

**DELETE** `/dealers/{id}`

```bash
curl -X DELETE http://localhost:8080/dealers/550e8400-e29b-41d4-a716-446655440000 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-Tenant-Id: tenant-123"
```

**Response** (204 No Content)

---

## Vehicle APIs

### 1. Create Vehicle

**POST** `/vehicles`

```bash
curl -X POST http://localhost:8080/vehicles \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-Tenant-Id: tenant-123" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "Toyota Camry 2024",
    "price": 28500.00,
    "status": "AVAILABLE",
    "dealerId": "550e8400-e29b-41d4-a716-446655440000"
  }'
```

**Response** (201 Created):
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "tenantId": "tenant-123",
  "model": "Toyota Camry 2024",
  "price": 28500.00,
  "status": "AVAILABLE",
  "dealerId": "550e8400-e29b-41d4-a716-446655440000",
  "dealerName": "Premium Auto Group",
  "createdAt": "2024-04-29T10:35:00",
  "updatedAt": "2024-04-29T10:35:00"
}
```

### 2. Get Vehicle by ID

**GET** `/vehicles/{id}`

```bash
curl -X GET http://localhost:8080/vehicles/660e8400-e29b-41d4-a716-446655440001 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-Tenant-Id: tenant-123"
```

**Response** (200 OK):
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "tenantId": "tenant-123",
  "model": "Toyota Camry 2024",
  "price": 28500.00,
  "status": "AVAILABLE",
  "dealerId": "550e8400-e29b-41d4-a716-446655440000",
  "dealerName": "Premium Auto Group",
  "createdAt": "2024-04-29T10:35:00",
  "updatedAt": "2024-04-29T10:35:00"
}
```

### 3. Get All Vehicles (with Filters)

**GET** `/vehicles?model=Toyota&status=AVAILABLE&priceMin=20000&priceMax=40000`

```bash
curl -X GET "http://localhost:8080/vehicles?model=Toyota&status=AVAILABLE&priceMin=20000&priceMax=40000&page=0&size=20&sort=price,asc" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-Tenant-Id: tenant-123"
```

**Response** (200 OK):
```json
{
  "content": [
    {
      "id": "660e8400-e29b-41d4-a716-446655440001",
      "tenantId": "tenant-123",
      "model": "Toyota Camry 2024",
      "price": 28500.00,
      "status": "AVAILABLE",
      "dealerId": "550e8400-e29b-41d4-a716-446655440000",
      "dealerName": "Premium Auto Group",
      "createdAt": "2024-04-29T10:35:00",
      "updatedAt": "2024-04-29T10:35:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalPages": 1,
  "totalElements": 1
}
```

### 4. Get Vehicles by Dealer Subscription

**GET** `/vehicles?subscription=PREMIUM`

```bash
curl -X GET "http://localhost:8080/vehicles?subscription=PREMIUM&page=0&size=20" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-Tenant-Id: tenant-123"
```

This returns all vehicles belonging to dealers with PREMIUM subscription within the current tenant.

### 5. Update Vehicle (Partial)

**PATCH** `/vehicles/{id}`

```bash
curl -X PATCH http://localhost:8080/vehicles/660e8400-e29b-41d4-a716-446655440001 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-Tenant-Id: tenant-123" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "SOLD",
    "price": 27000.00
  }'
```

**Response** (200 OK):
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "tenantId": "tenant-123",
  "model": "Toyota Camry 2024",
  "price": 27000.00,
  "status": "SOLD",
  "dealerId": "550e8400-e29b-41d4-a716-446655440000",
  "dealerName": "Premium Auto Group",
  "createdAt": "2024-04-29T10:35:00",
  "updatedAt": "2024-04-29T11:20:00"
}
```

### 6. Delete Vehicle

**DELETE** `/vehicles/{id}`

```bash
curl -X DELETE http://localhost:8080/vehicles/660e8400-e29b-41d4-a716-446655440001 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-Tenant-Id: tenant-123"
```

**Response** (204 No Content)

---

## Admin APIs

### Count Dealers by Subscription (GLOBAL)

**GET** `/admin/dealers/countBySubscription`

**Note**: Requires `ROLE_GLOBAL_ADMIN` role. Returns counts across ALL tenants.

```bash
curl -X GET http://localhost:8080/admin/dealers/countBySubscription \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN"
```

**Response** (200 OK):
```json
{
  "basic": 150,
  "premium": 75
}
```

**Important**: This endpoint does NOT require `X-Tenant-Id` header and returns global statistics.

---

## Error Examples

### 1. Missing Tenant Header

**Request**:
```bash
curl -X GET http://localhost:8080/dealers \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response** (400 Bad Request):
```json
{
  "timestamp": "2024-04-29T10:30:00",
  "status": 400,
  "message": "Tenant ID is required. Please provide X-Tenant-Id header.",
  "path": "/dealers"
}
```

### 2. Validation Error

**Request**:
```bash
curl -X POST http://localhost:8080/dealers \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-Tenant-Id: tenant-123" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "",
    "email": "invalid-email",
    "subscriptionType": null
  }'
```

**Response** (400 Bad Request):
```json
{
  "timestamp": "2024-04-29T10:30:00",
  "status": 400,
  "message": "Validation failed",
  "path": "/dealers",
  "errors": [
    {
      "field": "name",
      "message": "Name is required"
    },
    {
      "field": "email",
      "message": "Email must be valid"
    },
    {
      "field": "subscriptionType",
      "message": "Subscription type is required"
    }
  ]
}
```

### 3. Resource Not Found

**Request**:
```bash
curl -X GET http://localhost:8080/dealers/00000000-0000-0000-0000-000000000000 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-Tenant-Id: tenant-123"
```

**Response** (404 Not Found):
```json
{
  "timestamp": "2024-04-29T10:30:00",
  "status": 404,
  "message": "Dealer with id '00000000-0000-0000-0000-000000000000' not found",
  "path": "/dealers/00000000-0000-0000-0000-000000000000"
}
```

### 4. Cross-Tenant Access Attempt

**Request**: Try to access a dealer from tenant-A using tenant-B credentials
```bash
curl -X GET http://localhost:8080/dealers/dealer-from-tenant-A \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-Tenant-Id: tenant-B"
```

**Response** (404 Not Found):
```json
{
  "timestamp": "2024-04-29T10:30:00",
  "status": 404,
  "message": "Dealer with id 'dealer-from-tenant-A' not found",
  "path": "/dealers/dealer-from-tenant-A"
}
```

Note: Returns 404 (not 403) for security reasons - we don't reveal that the resource exists in another tenant.

### 5. Access Denied (Missing Admin Role)

**Request**:
```bash
curl -X GET http://localhost:8080/admin/dealers/countBySubscription \
  -H "Authorization: Bearer USER_JWT_TOKEN"
```

**Response** (403 Forbidden):
```json
{
  "timestamp": "2024-04-29T10:30:00",
  "status": 403,
  "message": "Access denied",
  "path": "/admin/dealers/countBySubscription"
}
```

---

## Postman Collection JSON

```json
{
  "info": {
    "name": "Multi-Tenant Inventory API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "variable": [
    {
      "key": "base_url",
      "value": "http://localhost:8080"
    },
    {
      "key": "jwt_token",
      "value": "YOUR_JWT_TOKEN"
    },
    {
      "key": "tenant_id",
      "value": "tenant-123"
    }
  ],
  "item": [
    {
      "name": "Dealers",
      "item": [
        {
          "name": "Create Dealer",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{jwt_token}}"
              },
              {
                "key": "X-Tenant-Id",
                "value": "{{tenant_id}}"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"name\": \"Premium Auto Group\",\n  \"email\": \"info@premiumauto.com\",\n  \"subscriptionType\": \"PREMIUM\"\n}",
              "options": {
                "raw": {
                  "language": "json"
                }
              }
            },
            "url": {
              "raw": "{{base_url}}/dealers",
              "host": ["{{base_url}}"],
              "path": ["dealers"]
            }
          }
        }
      ]
    }
  ]
}
```

---

## Testing Checklist

- [ ] Create dealer with valid data
- [ ] Create dealer with invalid email (validation error)
- [ ] Get dealer by ID
- [ ] Get all dealers with pagination
- [ ] Update dealer subscription type
- [ ] Delete dealer
- [ ] Create vehicle with valid dealer
- [ ] Create vehicle with non-existent dealer (should fail)
- [ ] Get vehicles with filters (model, status, price)
- [ ] Get vehicles by dealer subscription (PREMIUM)
- [ ] Update vehicle status to SOLD
- [ ] Delete vehicle
- [ ] Test cross-tenant access (should fail)
- [ ] Test missing tenant header (should return 400)
- [ ] Test admin endpoint with USER role (should return 403)
- [ ] Test admin endpoint with GLOBAL_ADMIN role (should succeed)