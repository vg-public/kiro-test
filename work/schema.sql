-- ============================================================
--  ShopWire — PostgreSQL DDL
--  Prefix: SW_
--  Generated: 2026-03-25
-- ============================================================

-- ── Extensions ──
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================
--  USERS & AUTH
-- ============================================================

CREATE TABLE SW_USER (
    user_id       UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    email         VARCHAR(255)  NOT NULL UNIQUE,
    password_hash VARCHAR(255)  NOT NULL,
    first_name    VARCHAR(100)  NOT NULL,
    last_name     VARCHAR(100)  NOT NULL,
    phone         VARCHAR(20),
    is_active     BOOLEAN       NOT NULL DEFAULT TRUE,
    is_verified   BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE TABLE SW_USER_ADDRESS (
    address_id    UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id       UUID          NOT NULL REFERENCES SW_USER(user_id) ON DELETE CASCADE,
    label         VARCHAR(50),                          -- e.g. Home, Work
    full_name     VARCHAR(200)  NOT NULL,
    line1         VARCHAR(255)  NOT NULL,
    line2         VARCHAR(255),
    city          VARCHAR(100)  NOT NULL,
    state         VARCHAR(100)  NOT NULL,
    postal_code   VARCHAR(20)   NOT NULL,
    country       CHAR(2)       NOT NULL DEFAULT 'US',  -- ISO 3166-1 alpha-2
    is_default    BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

-- ============================================================
--  CATALOG
-- ============================================================

CREATE TABLE SW_CATEGORY (
    category_id   SERIAL        PRIMARY KEY,
    parent_id     INT           REFERENCES SW_CATEGORY(category_id) ON DELETE SET NULL,
    name          VARCHAR(150)  NOT NULL,
    slug          VARCHAR(150)  NOT NULL UNIQUE,
    description   TEXT,
    image_url     VARCHAR(500),
    sort_order    INT           NOT NULL DEFAULT 0,
    is_active     BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE TABLE SW_BRAND (
    brand_id      SERIAL        PRIMARY KEY,
    name          VARCHAR(150)  NOT NULL UNIQUE,
    slug          VARCHAR(150)  NOT NULL UNIQUE,
    logo_url      VARCHAR(500),
    is_active     BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE TABLE SW_PRODUCT (
    product_id    UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    category_id   INT           NOT NULL REFERENCES SW_CATEGORY(category_id),
    brand_id      INT           REFERENCES SW_BRAND(brand_id) ON DELETE SET NULL,
    seller_id     UUID          REFERENCES SW_USER(user_id) ON DELETE SET NULL,
    title         VARCHAR(500)  NOT NULL,
    slug          VARCHAR(500)  NOT NULL UNIQUE,
    description   TEXT,
    bullet_points TEXT[],                               -- feature bullet list
    base_price    NUMERIC(12,2) NOT NULL CHECK (base_price >= 0),
    sale_price    NUMERIC(12,2)          CHECK (sale_price >= 0),
    currency      CHAR(3)       NOT NULL DEFAULT 'USD',
    is_prime      BOOLEAN       NOT NULL DEFAULT FALSE,
    is_active     BOOLEAN       NOT NULL DEFAULT TRUE,
    is_featured   BOOLEAN       NOT NULL DEFAULT FALSE,
    badge         VARCHAR(50),                          -- e.g. Best Seller, Deal
    avg_rating    NUMERIC(3,2)  NOT NULL DEFAULT 0.00,
    review_count  INT           NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE TABLE SW_PRODUCT_IMAGE (
    image_id      SERIAL        PRIMARY KEY,
    product_id    UUID          NOT NULL REFERENCES SW_PRODUCT(product_id) ON DELETE CASCADE,
    url           VARCHAR(500)  NOT NULL,
    alt_text      VARCHAR(255),
    sort_order    INT           NOT NULL DEFAULT 0,
    is_primary    BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE TABLE SW_PRODUCT_VARIANT (
    variant_id    UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id    UUID          NOT NULL REFERENCES SW_PRODUCT(product_id) ON DELETE CASCADE,
    sku           VARCHAR(100)  NOT NULL UNIQUE,
    title         VARCHAR(255)  NOT NULL,               -- e.g. "Red / XL"
    price         NUMERIC(12,2) NOT NULL CHECK (price >= 0),
    stock_qty     INT           NOT NULL DEFAULT 0 CHECK (stock_qty >= 0),
    is_active     BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE TABLE SW_PRODUCT_ATTRIBUTE (
    attribute_id  SERIAL        PRIMARY KEY,
    name          VARCHAR(100)  NOT NULL UNIQUE         -- e.g. Color, Size, Storage
);

CREATE TABLE SW_VARIANT_ATTRIBUTE_VALUE (
    variant_id    UUID          NOT NULL REFERENCES SW_PRODUCT_VARIANT(variant_id) ON DELETE CASCADE,
    attribute_id  INT           NOT NULL REFERENCES SW_PRODUCT_ATTRIBUTE(attribute_id) ON DELETE CASCADE,
    value         VARCHAR(100)  NOT NULL,               -- e.g. Red, XL, 256GB
    PRIMARY KEY (variant_id, attribute_id)
);

-- ============================================================
--  INVENTORY
-- ============================================================

CREATE TABLE SW_INVENTORY (
    inventory_id  SERIAL        PRIMARY KEY,
    variant_id    UUID          NOT NULL UNIQUE REFERENCES SW_PRODUCT_VARIANT(variant_id) ON DELETE CASCADE,
    qty_available INT           NOT NULL DEFAULT 0 CHECK (qty_available >= 0),
    qty_reserved  INT           NOT NULL DEFAULT 0 CHECK (qty_reserved >= 0),
    reorder_level INT           NOT NULL DEFAULT 5,
    updated_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

-- ============================================================
--  REVIEWS & RATINGS
-- ============================================================

CREATE TABLE SW_REVIEW (
    review_id     UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id    UUID          NOT NULL REFERENCES SW_PRODUCT(product_id) ON DELETE CASCADE,
    user_id       UUID          NOT NULL REFERENCES SW_USER(user_id) ON DELETE CASCADE,
    rating        SMALLINT      NOT NULL CHECK (rating BETWEEN 1 AND 5),
    title         VARCHAR(255),
    body          TEXT,
    is_verified   BOOLEAN       NOT NULL DEFAULT FALSE, -- verified purchase
    helpful_count INT           NOT NULL DEFAULT 0,
    created_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    UNIQUE (product_id, user_id)                        -- one review per user per product
);

-- ============================================================
--  CART
-- ============================================================

CREATE TABLE SW_CART (
    cart_id       UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id       UUID          UNIQUE REFERENCES SW_USER(user_id) ON DELETE CASCADE,
    session_id    VARCHAR(255)  UNIQUE,                 -- for guest carts
    created_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    CONSTRAINT cart_owner CHECK (user_id IS NOT NULL OR session_id IS NOT NULL)
);

CREATE TABLE SW_CART_ITEM (
    cart_item_id  SERIAL        PRIMARY KEY,
    cart_id       UUID          NOT NULL REFERENCES SW_CART(cart_id) ON DELETE CASCADE,
    variant_id    UUID          NOT NULL REFERENCES SW_PRODUCT_VARIANT(variant_id),
    quantity      INT           NOT NULL DEFAULT 1 CHECK (quantity > 0),
    added_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    UNIQUE (cart_id, variant_id)
);

-- ============================================================
--  ORDERS
-- ============================================================

CREATE TYPE sw_order_status AS ENUM (
    'pending', 'confirmed', 'processing',
    'shipped', 'delivered', 'cancelled', 'refunded'
);

CREATE TABLE SW_ORDER (
    order_id          UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id           UUID            NOT NULL REFERENCES SW_USER(user_id),
    shipping_address  JSONB           NOT NULL,          -- snapshot at time of order
    status            sw_order_status NOT NULL DEFAULT 'pending',
    subtotal          NUMERIC(12,2)   NOT NULL,
    shipping_cost     NUMERIC(12,2)   NOT NULL DEFAULT 0,
    tax_amount        NUMERIC(12,2)   NOT NULL DEFAULT 0,
    discount_amount   NUMERIC(12,2)   NOT NULL DEFAULT 0,
    total_amount      NUMERIC(12,2)   NOT NULL,
    currency          CHAR(3)         NOT NULL DEFAULT 'USD',
    notes             TEXT,
    created_at        TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE TABLE SW_ORDER_ITEM (
    order_item_id   SERIAL        PRIMARY KEY,
    order_id        UUID          NOT NULL REFERENCES SW_ORDER(order_id) ON DELETE CASCADE,
    variant_id      UUID          NOT NULL REFERENCES SW_PRODUCT_VARIANT(variant_id),
    product_title   VARCHAR(500)  NOT NULL,              -- snapshot
    variant_title   VARCHAR(255)  NOT NULL,              -- snapshot
    unit_price      NUMERIC(12,2) NOT NULL,
    quantity        INT           NOT NULL CHECK (quantity > 0),
    line_total      NUMERIC(12,2) NOT NULL
);

-- ============================================================
--  PAYMENTS
-- ============================================================

CREATE TYPE sw_payment_status AS ENUM (
    'pending', 'authorized', 'captured', 'failed', 'refunded'
);

CREATE TABLE SW_PAYMENT (
    payment_id        UUID              PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id          UUID              NOT NULL REFERENCES SW_ORDER(order_id),
    amount            NUMERIC(12,2)     NOT NULL,
    currency          CHAR(3)           NOT NULL DEFAULT 'USD',
    status            sw_payment_status NOT NULL DEFAULT 'pending',
    provider          VARCHAR(50),                       -- e.g. stripe, paypal
    provider_ref      VARCHAR(255),                      -- external transaction id
    paid_at           TIMESTAMPTZ,
    created_at        TIMESTAMPTZ       NOT NULL DEFAULT NOW()
);

-- ============================================================
--  SHIPPING
-- ============================================================

CREATE TABLE SW_SHIPMENT (
    shipment_id     UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id        UUID          NOT NULL REFERENCES SW_ORDER(order_id),
    carrier         VARCHAR(100),
    tracking_number VARCHAR(200),
    shipped_at      TIMESTAMPTZ,
    estimated_at    TIMESTAMPTZ,
    delivered_at    TIMESTAMPTZ,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

-- ============================================================
--  PROMOTIONS
-- ============================================================

CREATE TYPE sw_discount_type AS ENUM ('percentage', 'fixed_amount', 'free_shipping');

CREATE TABLE SW_COUPON (
    coupon_id       SERIAL            PRIMARY KEY,
    code            VARCHAR(50)       NOT NULL UNIQUE,
    discount_type   sw_discount_type  NOT NULL,
    discount_value  NUMERIC(10,2)     NOT NULL CHECK (discount_value > 0),
    min_order_value NUMERIC(12,2)     NOT NULL DEFAULT 0,
    max_uses        INT,
    used_count      INT               NOT NULL DEFAULT 0,
    valid_from      TIMESTAMPTZ       NOT NULL DEFAULT NOW(),
    valid_until     TIMESTAMPTZ,
    is_active       BOOLEAN           NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ       NOT NULL DEFAULT NOW()
);

-- ============================================================
--  SEARCH / WISHLIST
-- ============================================================

CREATE TABLE SW_WISHLIST (
    wishlist_id   SERIAL        PRIMARY KEY,
    user_id       UUID          NOT NULL REFERENCES SW_USER(user_id) ON DELETE CASCADE,
    variant_id    UUID          NOT NULL REFERENCES SW_PRODUCT_VARIANT(variant_id) ON DELETE CASCADE,
    added_at      TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, variant_id)
);

CREATE TABLE SW_SEARCH_LOG (
    search_id     BIGSERIAL     PRIMARY KEY,
    user_id       UUID          REFERENCES SW_USER(user_id) ON DELETE SET NULL,
    session_id    VARCHAR(255),
    query         VARCHAR(500)  NOT NULL,
    result_count  INT,
    searched_at   TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

-- ============================================================
--  INDEXES
-- ============================================================

-- Product search & filtering
CREATE INDEX idx_sw_product_category   ON SW_PRODUCT(category_id);
CREATE INDEX idx_sw_product_brand      ON SW_PRODUCT(brand_id);
CREATE INDEX idx_sw_product_active     ON SW_PRODUCT(is_active);
CREATE INDEX idx_sw_product_featured   ON SW_PRODUCT(is_featured);
CREATE INDEX idx_sw_product_title_fts  ON SW_PRODUCT USING GIN (to_tsvector('english', title));

-- Orders
CREATE INDEX idx_sw_order_user         ON SW_ORDER(user_id);
CREATE INDEX idx_sw_order_status       ON SW_ORDER(status);

-- Cart
CREATE INDEX idx_sw_cart_item_cart     ON SW_CART_ITEM(cart_id);

-- Reviews
CREATE INDEX idx_sw_review_product     ON SW_REVIEW(product_id);

-- Search log
CREATE INDEX idx_sw_search_log_query   ON SW_SEARCH_LOG(query);
CREATE INDEX idx_sw_search_log_user    ON SW_SEARCH_LOG(user_id);

-- ============================================================
--  AUTO-UPDATE updated_at TRIGGER
-- ============================================================

CREATE OR REPLACE FUNCTION sw_set_updated_at()
RETURNS TRIGGER LANGUAGE plpgsql AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$;

CREATE TRIGGER trg_sw_user_updated_at
  BEFORE UPDATE ON SW_USER
  FOR EACH ROW EXECUTE FUNCTION sw_set_updated_at();

CREATE TRIGGER trg_sw_product_updated_at
  BEFORE UPDATE ON SW_PRODUCT
  FOR EACH ROW EXECUTE FUNCTION sw_set_updated_at();

CREATE TRIGGER trg_sw_order_updated_at
  BEFORE UPDATE ON SW_ORDER
  FOR EACH ROW EXECUTE FUNCTION sw_set_updated_at();

CREATE TRIGGER trg_sw_cart_updated_at
  BEFORE UPDATE ON SW_CART
  FOR EACH ROW EXECUTE FUNCTION sw_set_updated_at();
