-- ============================================================
--  ShopWire — DROP ALL (Nuclear)
--  Tears down every SW_ table, type, index, trigger, and function
--  Run this to fully reset the schema before re-running schema.sql
--  ⚠️  IRREVERSIBLE — all data will be lost
-- ============================================================

-- ── Tables (child → parent order) ───────────────────────────
DROP TABLE IF EXISTS SW_SEARCH_LOG              CASCADE;
DROP TABLE IF EXISTS SW_WISHLIST                CASCADE;
DROP TABLE IF EXISTS SW_COUPON                  CASCADE;
DROP TABLE IF EXISTS SW_SHIPMENT                CASCADE;
DROP TABLE IF EXISTS SW_PAYMENT                 CASCADE;
DROP TABLE IF EXISTS SW_ORDER_ITEM              CASCADE;
DROP TABLE IF EXISTS SW_ORDER                   CASCADE;
DROP TABLE IF EXISTS SW_CART_ITEM               CASCADE;
DROP TABLE IF EXISTS SW_CART                    CASCADE;
DROP TABLE IF EXISTS SW_REVIEW                  CASCADE;
DROP TABLE IF EXISTS SW_INVENTORY               CASCADE;
DROP TABLE IF EXISTS SW_VARIANT_ATTRIBUTE_VALUE CASCADE;
DROP TABLE IF EXISTS SW_PRODUCT_ATTRIBUTE       CASCADE;
DROP TABLE IF EXISTS SW_PRODUCT_VARIANT         CASCADE;
DROP TABLE IF EXISTS SW_PRODUCT_IMAGE           CASCADE;
DROP TABLE IF EXISTS SW_PRODUCT                 CASCADE;
DROP TABLE IF EXISTS SW_BRAND                   CASCADE;
DROP TABLE IF EXISTS SW_CATEGORY                CASCADE;
DROP TABLE IF EXISTS SW_USER_ADDRESS            CASCADE;
DROP TABLE IF EXISTS SW_USER                    CASCADE;

-- ── ENUM types ───────────────────────────────────────────────
DROP TYPE IF EXISTS sw_order_status   CASCADE;
DROP TYPE IF EXISTS sw_payment_status CASCADE;
DROP TYPE IF EXISTS sw_discount_type  CASCADE;

-- ── Trigger function ─────────────────────────────────────────
DROP FUNCTION IF EXISTS sw_set_updated_at() CASCADE;
