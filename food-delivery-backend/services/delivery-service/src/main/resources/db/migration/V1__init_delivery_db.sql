
CREATE TABLE driver_profiles (
                                 driver_id UUID PRIMARY KEY,
                                 full_name VARCHAR(100) NOT NULL,
                                 license_plate VARCHAR(20) UNIQUE NOT NULL,
                                 vehicle_type VARCHAR(20) NOT NULL,
                                 is_online BOOLEAN DEFAULT FALSE,
                                 status VARCHAR(20) DEFAULT 'AVAILABLE',
                                 created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                 deleted_at TIMESTAMPTZ
);

CREATE INDEX idx_driver_is_online ON driver_profiles(is_online);
CREATE INDEX idx_driver_status ON driver_profiles(status);

CREATE TABLE deliveries (
                            id UUID PRIMARY KEY,
                            order_id UUID UNIQUE NOT NULL,
                            driver_id UUID NOT NULL,
                            status VARCHAR(30) NOT NULL,
                            pickup_time TIMESTAMPTZ,
                            dropoff_time TIMESTAMPTZ,
                            created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                            CONSTRAINT fk_deliveries_driver FOREIGN KEY (driver_id) REFERENCES driver_profiles(driver_id)
);

CREATE INDEX idx_deliveries_status ON deliveries(status);