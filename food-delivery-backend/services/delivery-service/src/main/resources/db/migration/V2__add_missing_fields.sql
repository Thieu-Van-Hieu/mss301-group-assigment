-- ==========================================
-- DRIVER PROFILES
-- ==========================================

ALTER TABLE driver_profiles
    ADD COLUMN phone_number VARCHAR(15),
    ADD COLUMN email VARCHAR(100),
    ADD COLUMN identity_number VARCHAR(20),
    ADD COLUMN license_number VARCHAR(20),
    ADD COLUMN vehicle_color VARCHAR(30),
    ADD COLUMN wallet_balance NUMERIC(15,2) DEFAULT 0.00;

-- Cập nhật default status mới
ALTER TABLE driver_profiles
    ALTER COLUMN status SET DEFAULT 'PENDING_ONBOARDING';

-- ==========================================
-- DELIVERIES
-- ==========================================

ALTER TABLE deliveries
    ADD COLUMN pickup_address TEXT,
    ADD COLUMN dropoff_address TEXT,
    ADD COLUMN cod_amount NUMERIC(15,2) DEFAULT 0.00,
    ADD COLUMN delivery_fee NUMERIC(15,2) DEFAULT 0.00,
    ADD COLUMN reason_failed TEXT;

-- driver_id được phép NULL
ALTER TABLE deliveries
    ALTER COLUMN driver_id DROP NOT NULL;

-- order_id bỏ UNIQUE nếu business mới cho phép
ALTER TABLE deliveries
DROP CONSTRAINT IF EXISTS deliveries_order_id_key;