-- ==========================================
-- RESTAURANTS
-- ==========================================
CREATE TABLE restaurants (
    id            UUID PRIMARY KEY,
    owner_id      UUID,
    name          VARCHAR(150) NOT NULL,
    address       TEXT NOT NULL,
    image_url     TEXT,
    description   TEXT,
    cuisine_type  VARCHAR(100),
    status        VARCHAR(20) NOT NULL DEFAULT 'CLOSED',
    opening_time  TIME,
    closing_time  TIME,
    created_at    TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMPTZ
);

CREATE INDEX idx_restaurants_name ON restaurants (name);
CREATE INDEX idx_restaurants_cuisine ON restaurants (cuisine_type);
CREATE INDEX idx_restaurants_status ON restaurants (status);

-- ==========================================
-- CATEGORIES (phân loại món ăn theo từng nhà hàng)
-- ==========================================
CREATE TABLE categories (
    id            UUID PRIMARY KEY,
    restaurant_id UUID NOT NULL,
    name          VARCHAR(100) NOT NULL,
    created_at    TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_categories_restaurant FOREIGN KEY (restaurant_id)
        REFERENCES restaurants (id) ON DELETE CASCADE
);

CREATE INDEX idx_categories_restaurant ON categories (restaurant_id);

-- ==========================================
-- MENU ITEMS (món ăn)
-- ==========================================
CREATE TABLE menu_items (
    id            UUID PRIMARY KEY,
    restaurant_id UUID NOT NULL,
    category_id   UUID,
    name          VARCHAR(150) NOT NULL,
    description   TEXT,
    price         NUMERIC(15, 2) NOT NULL,
    image_url     TEXT,
    available     BOOLEAN DEFAULT TRUE,
    created_at    TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMPTZ,
    CONSTRAINT fk_menu_items_restaurant FOREIGN KEY (restaurant_id)
        REFERENCES restaurants (id) ON DELETE CASCADE,
    CONSTRAINT fk_menu_items_category FOREIGN KEY (category_id)
        REFERENCES categories (id) ON DELETE SET NULL
);

CREATE INDEX idx_menu_items_restaurant ON menu_items (restaurant_id);
CREATE INDEX idx_menu_items_category ON menu_items (category_id);
CREATE INDEX idx_menu_items_name ON menu_items (name);
CREATE INDEX idx_menu_items_price ON menu_items (price);
