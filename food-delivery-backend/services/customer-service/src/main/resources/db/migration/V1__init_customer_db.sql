-- ==========================================
-- CUSTOMERS (thông tin tài khoản khách hàng, id = userId từ identity-service)
-- ==========================================
CREATE TABLE customers (
    id           UUID PRIMARY KEY,
    full_name    VARCHAR(100),
    email        VARCHAR(100),
    phone_number VARCHAR(15),
    created_at   TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMPTZ
);

CREATE INDEX idx_customers_email ON customers (email);

-- ==========================================
-- ADDRESSES (địa chỉ giao hàng của khách)
-- ==========================================
CREATE TABLE addresses (
    id             UUID PRIMARY KEY,
    customer_id    UUID NOT NULL,
    recipient_name VARCHAR(100),
    phone_number   VARCHAR(15),
    address_line   TEXT NOT NULL,
    ward           VARCHAR(100),
    district       VARCHAR(100),
    city           VARCHAR(100),
    latitude       NUMERIC(10, 8),
    longitude      NUMERIC(11, 8),
    is_default     BOOLEAN DEFAULT FALSE,
    created_at     TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_addresses_customer FOREIGN KEY (customer_id)
        REFERENCES customers (id) ON DELETE CASCADE
);

CREATE INDEX idx_addresses_customer ON addresses (customer_id);

-- ==========================================
-- ORDER_HISTORY (read model, dựng từ event của Order Service)
-- ==========================================
CREATE TABLE order_history (
    order_id      UUID PRIMARY KEY,
    customer_id   UUID NOT NULL,
    restaurant_id UUID,
    status        VARCHAR(30) NOT NULL,
    total_amount  NUMERIC(15, 2),
    currency      VARCHAR(10),
    items_summary TEXT,
    created_at    TIMESTAMPTZ,
    completed_at  TIMESTAMPTZ
);

CREATE INDEX idx_order_history_customer ON order_history (customer_id);
CREATE INDEX idx_order_history_status ON order_history (status);
