-- Multi-Tenant Inventory System - Database Schema
-- PostgreSQL 16+

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================
-- DEALERS TABLE
-- ============================================

CREATE TABLE dealers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    subscription_type VARCHAR(20) NOT NULL CHECK (subscription_type IN ('BASIC', 'PREMIUM')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_dealer_tenant ON dealers(tenant_id);
CREATE INDEX idx_dealer_email ON dealers(email);
CREATE INDEX idx_dealer_subscription ON dealers(subscription_type);
CREATE INDEX idx_dealer_created_at ON dealers(created_at DESC);

-- Composite index for tenant-scoped queries
CREATE INDEX idx_dealer_tenant_id ON dealers(tenant_id, id);

-- Comments
COMMENT ON TABLE dealers IS 'Stores dealer information with multi-tenant support';
COMMENT ON COLUMN dealers.tenant_id IS 'Tenant identifier for data isolation';
COMMENT ON COLUMN dealers.subscription_type IS 'Dealer subscription tier: BASIC or PREMIUM';

-- ============================================
-- VEHICLES TABLE
-- ============================================

CREATE TABLE vehicles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id VARCHAR(255) NOT NULL,
    dealer_id UUID NOT NULL,
    model VARCHAR(255) NOT NULL,
    price DECIMAL(19,2) NOT NULL CHECK (price > 0),
    status VARCHAR(20) NOT NULL CHECK (status IN ('AVAILABLE', 'SOLD')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign key constraint
    CONSTRAINT fk_vehicle_dealer FOREIGN KEY (dealer_id)
        REFERENCES dealers(id)
        ON DELETE CASCADE
);

-- Indexes for performance
CREATE INDEX idx_vehicle_tenant ON vehicles(tenant_id);
CREATE INDEX idx_vehicle_dealer ON vehicles(dealer_id);
CREATE INDEX idx_vehicle_status ON vehicles(status);
CREATE INDEX idx_vehicle_model ON vehicles(model);
CREATE INDEX idx_vehicle_price ON vehicles(price);
CREATE INDEX idx_vehicle_created_at ON vehicles(created_at DESC);

-- Composite indexes for common queries
CREATE INDEX idx_vehicle_tenant_id ON vehicles(tenant_id, id);
CREATE INDEX idx_vehicle_tenant_status ON vehicles(tenant_id, status);
CREATE INDEX idx_vehicle_tenant_dealer ON vehicles(tenant_id, dealer_id);

-- Comments
COMMENT ON TABLE vehicles IS 'Stores vehicle inventory with multi-tenant support';
COMMENT ON COLUMN vehicles.tenant_id IS 'Tenant identifier for data isolation';
COMMENT ON COLUMN vehicles.dealer_id IS 'Reference to the owning dealer';
COMMENT ON COLUMN vehicles.status IS 'Current status: AVAILABLE or SOLD';
COMMENT ON COLUMN vehicles.price IS 'Vehicle price in decimal format';

-- ============================================
-- TRIGGERS FOR UPDATED_AT
-- ============================================

-- Function to update the updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger for dealers table
CREATE TRIGGER update_dealers_updated_at
    BEFORE UPDATE ON dealers
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Trigger for vehicles table
CREATE TRIGGER update_vehicles_updated_at
    BEFORE UPDATE ON vehicles
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- SAMPLE DATA (for testing)
-- ============================================

-- Insert sample dealers
INSERT INTO dealers (id, tenant_id, name, email, subscription_type, created_at, updated_at)
VALUES
    ('550e8400-e29b-41d4-a716-446655440000', 'tenant-123', 'Premium Auto Group', 'info@premiumauto.com', 'PREMIUM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440001', 'tenant-123', 'Budget Cars Inc', 'contact@budgetcars.com', 'BASIC', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('550e8400-e29b-41d4-a716-446655440002', 'tenant-456', 'Elite Motors', 'sales@elitemotors.com', 'PREMIUM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample vehicles
INSERT INTO vehicles (id, tenant_id, dealer_id, model, price, status, created_at, updated_at)
VALUES
    ('660e8400-e29b-41d4-a716-446655440001', 'tenant-123', '550e8400-e29b-41d4-a716-446655440000', 'Toyota Camry 2024', 28500.00, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('660e8400-e29b-41d4-a716-446655440002', 'tenant-123', '550e8400-e29b-41d4-a716-446655440000', 'Honda Accord 2024', 30500.00, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('660e8400-e29b-41d4-a716-446655440003', 'tenant-123', '550e8400-e29b-41d4-a716-446655440001', 'Ford Focus 2023', 22000.00, 'SOLD', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('660e8400-e29b-41d4-a716-446655440004', 'tenant-456', '550e8400-e29b-41d4-a716-446655440002', 'BMW 5 Series 2024', 55000.00, 'AVAILABLE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ============================================
-- USEFUL QUERIES
-- ============================================

-- Count dealers by subscription type (global)
-- SELECT subscription_type, COUNT(*) FROM dealers GROUP BY subscription_type;

-- Get all vehicles for a tenant
-- SELECT * FROM vehicles WHERE tenant_id = 'tenant-123';

-- Get vehicles by dealer subscription (within tenant)
-- SELECT v.*
-- FROM vehicles v
-- JOIN dealers d ON v.dealer_id = d.id
-- WHERE v.tenant_id = 'tenant-123'
--   AND d.subscription_type = 'PREMIUM';

-- Get available vehicles in price range
-- SELECT * FROM vehicles
-- WHERE tenant_id = 'tenant-123'
--   AND status = 'AVAILABLE'
--   AND price BETWEEN 20000 AND 40000
-- ORDER BY price ASC;

-- ============================================
-- MAINTENANCE QUERIES
-- ============================================

-- Analyze table statistics (run periodically)
-- ANALYZE dealers;
-- ANALYZE vehicles;

-- Check table sizes
-- SELECT
--     schemaname,
--     tablename,
--     pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
-- FROM pg_tables
-- WHERE schemaname = 'public'
-- ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;

-- Check index usage
-- SELECT
--     schemaname,
--     tablename,
--     indexname,
--     idx_scan as index_scans,
--     pg_size_pretty(pg_relation_size(indexrelid)) as index_size
-- FROM pg_stat_user_indexes
-- WHERE schemaname = 'public'
-- ORDER BY idx_scan DESC;

-- ============================================
-- CLEANUP (for development)
-- ============================================

-- Drop tables (use with caution!)
-- DROP TABLE IF EXISTS vehicles CASCADE;
-- DROP TABLE IF EXISTS dealers CASCADE;
-- DROP FUNCTION IF EXISTS update_updated_at_column() CASCADE;