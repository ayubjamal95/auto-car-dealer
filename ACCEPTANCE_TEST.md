# Acceptance Test Scenarios

This document outlines acceptance test scenarios to verify multi-tenancy and admin endpoint behavior.

## Test Scenario 1: Missing X-Tenant-Id → 400 (Regular Endpoints)

### Test Case 1.1: Dealer Endpoint Without Tenant Header

**Request:**
```bash
curl -X GET http://localhost:8080/dealers \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected Result:**
- HTTP Status: `400 Bad Request`
- Response Body:
```json
{
  "timestamp": "2024-04-29T10:30:00",
  "status": 400,
  "message": "Tenant ID is required. Please provide X-Tenant-Id header.",
  "path": "/dealers"
}
```

### Test Case 1.2: Vehicle Endpoint Without Tenant Header

**Request:**
```bash
curl -X GET http://localhost:8080/vehicles \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected Result:**
- HTTP Status: `400 Bad Request`
- Response Body:
```json
{
  "timestamp": "2024-04-29T10:30:00",
  "status": 400,
  "message": "Tenant ID is required. Please provide X-Tenant-Id header.",
  "path": "/vehicles"
}
```

---

## Test Scenario 2: Admin Endpoints - No Tenant Required

### Test Case 2.1: Admin Endpoint WITHOUT X-Tenant-Id Header (Correct)

**Request:**
```bash
curl -X GET http://localhost:8080/admin/dealers/countBySubscription \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN"
```

**Expected Result:**
- HTTP Status: `200 OK`
- Response Body:
```json
{
  "basic": 150,
  "premium": 75
}
```

**Note:** This should work because admin endpoints are GLOBAL and don't require tenant context.

### Test Case 2.2: Admin Endpoint WITH X-Tenant-Id Header (Also Works)

**Request:**
```bash
curl -X GET http://localhost:8080/admin/dealers/countBySubscription \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN" \
  -H "X-Tenant-Id: tenant-123"
```

**Expected Result:**
- HTTP Status: `200 OK`
- Response Body:
```json
{
  "basic": 150,
  "premium": 75
}
```

**Note:** This also works, but the tenant header is ignored since the endpoint returns global data.

### Test Case 2.3: Admin Endpoint WITHOUT Admin Role (403)

**Request:**
```bash
curl -X GET http://localhost:8080/admin/dealers/countBySubscription \
  -H "Authorization: Bearer YOUR_USER_JWT_TOKEN"
```

**Expected Result:**
- HTTP Status: `403 Forbidden`
- Response Body:
```json
{
  "timestamp": "2024-04-29T10:30:00",
  "status": 403,
  "message": "Access denied",
  "path": "/admin/dealers/countBySubscription"
}
```

---

## Test Scenario 3: Cross-Tenant Access → 404 (Not 403)

### Test Case 3.1: Access Dealer from Different Tenant

**Setup:**
1. Create a dealer in tenant-A
2. Try to access it from tenant-B

**Request:**
```bash
# Dealer created in tenant-A with ID: dealer-A-id
curl -X GET http://localhost:8080/dealers/dealer-A-id \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-Tenant-Id: tenant-B"
```

**Expected Result:**
- HTTP Status: `404 Not Found`
- Response Body:
```json
{
  "timestamp": "2024-04-29T10:30:00",
  "status": 404,
  "message": "Dealer with id 'dealer-A-id' not found",
  "path": "/dealers/dealer-A-id"
}
```

**Rationale:** Returns 404 (not 403) for security - we don't reveal that the resource exists in another tenant.

---

## Test Scenario 4: Tenant Isolation Verification

### Test Case 4.1: Create Data in Multiple Tenants

**Step 1: Create Dealer in Tenant A**
```bash
curl -X POST http://localhost:8080/dealers \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-Tenant-Id: tenant-A" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Dealer A",
    "email": "dealer-a@example.com",
    "subscriptionType": "PREMIUM"
  }'
```

**Step 2: Create Dealer in Tenant B**
```bash
curl -X POST http://localhost:8080/dealers \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-Tenant-Id: tenant-B" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Dealer B",
    "email": "dealer-b@example.com",
    "subscriptionType": "BASIC"
  }'
```

**Step 3: Verify Isolation - Get Dealers for Tenant A**
```bash
curl -X GET http://localhost:8080/dealers \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-Tenant-Id: tenant-A"
```

**Expected:** Only returns dealers from tenant-A

**Step 4: Verify Isolation - Get Dealers for Tenant B**
```bash
curl -X GET http://localhost:8080/dealers \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "X-Tenant-Id: tenant-B"
```

**Expected:** Only returns dealers from tenant-B

**Step 5: Admin Sees All**
```bash
curl -X GET http://localhost:8080/admin/dealers/countBySubscription \
  -H "Authorization: Bearer YOUR_ADMIN_JWT_TOKEN"
```

**Expected:** Returns counts from ALL tenants
```json
{
  "basic": 1,    // Dealer B
  "premium": 1   // Dealer A
}
```

---

## Summary of Expected Behaviors

| Scenario | Endpoint Type | With Tenant Header | Without Tenant Header |
|----------|---------------|-------------------|----------------------|
| Regular Endpoints (Dealers, Vehicles) | Tenant-scoped | ✅ 200 OK (tenant-filtered) | ❌ 400 Bad Request |
| Admin Endpoints | Global | ✅ 200 OK (ignores header) | ✅ 200 OK (global data) |
| Admin Without Role | Global | ❌ 403 Forbidden | ❌ 403 Forbidden |
| Cross-Tenant Access | Tenant-scoped | ❌ 404 Not Found | N/A |

---

## Testing Checklist

### Multi-Tenancy Tests
- [ ] Regular endpoints without tenant header return 400
- [ ] Regular endpoints with tenant header return 200
- [ ] Data is properly isolated by tenant
- [ ] Cross-tenant access returns 404
- [ ] Creating resources assigns correct tenant ID

### Admin Endpoint Tests
- [ ] Admin endpoint works WITHOUT tenant header
- [ ] Admin endpoint returns GLOBAL data (all tenants)
- [ ] Admin endpoint requires GLOBAL_ADMIN role
- [ ] Regular users cannot access admin endpoints (403)

### Security Tests
- [ ] JWT validation is enforced
- [ ] Role-based access control works
- [ ] Missing authentication returns 401/403
- [ ] Invalid JWT returns 403

### Data Integrity Tests
- [ ] All entities have tenantId field populated
- [ ] Repository queries filter by tenant
- [ ] Service layer validates tenant ownership
- [ ] Relationships respect tenant boundaries

---

## How to Generate Test JWTs

### For Regular User (ROLE_USER)

Payload:
```json
{
  "sub": "testuser",
  "roles": "ROLE_USER",
  "iat": 1714397400,
  "exp": 9999999999
}
```

Secret: `9f3c2a7e5d1b4f8c6a0e2d9b7c3f1a6e8d4c5b2a7f9e1c3d6b8a0f2e4c7d9b1a` (from application.yml)

### For Admin (ROLE_GLOBAL_ADMIN)

Payload:
```json
{
  "sub": "adminuser",
  "roles": "ROLE_GLOBAL_ADMIN",
  "iat": 1714397400,
  "exp": 9999999999
}
```

Secret: `9f3c2a7e5d1b4f8c6a0e2d9b7c3f1a6e8d4c5b2a7f9e1c3d6b8a0f2e4c7d9b1a` (from application.yml)

You can use https://jwt.io to generate these tokens.

---

## Troubleshooting

### Issue: Admin endpoint returns 400 (Missing Tenant)

**Cause:** `/admin` path not added to `PUBLIC_PATHS` in `TenantFilter.java`

**Solution:** Verify that `TenantFilter.java` includes `/admin` in the `PUBLIC_PATHS` array:
```java
private static final String[] PUBLIC_PATHS = {
    "/actuator",
    "/swagger-ui",
    "/api-docs",
    "/v3/api-docs",
    "/admin"  // Admin endpoints are global
};
```

### Issue: Admin endpoint returns 403 without tenant header

**Cause:** Spring Security is processing the request before tenant filter

**Solution:** This is the expected behavior if you don't have a valid JWT token or if the token doesn't have `ROLE_GLOBAL_ADMIN`.

### Issue: Regular endpoints work without tenant header

**Cause:** Path might be in PUBLIC_PATHS or filter not registered

**Solution:** Verify that the endpoint path is not in PUBLIC_PATHS and that TenantFilter is registered as a Spring component.

---

**Test Status:** ✅ All scenarios should pass with the current implementation