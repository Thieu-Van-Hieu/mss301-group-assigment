ALTER TABLE deliveries
    ADD COLUMN pickup_lat NUMERIC(10,8),
    ADD COLUMN pickup_lng NUMERIC(11,8),
    ADD COLUMN dropoff_lat NUMERIC(10,8),
    ADD COLUMN dropoff_lng NUMERIC(11,8);