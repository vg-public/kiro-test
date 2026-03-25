# ShopWire — Database Schema Reference

PostgreSQL · All tables prefixed `SW_` · File: `schema.sql`

---

## Users & Auth

### SW_USER
Core account table. One row per registered user or seller.
- `user_id` — UUID PK, used as FK across the entire schema
- `email` — unique login identifier
- `password_hash` — bcrypt/argon2 hash, never plain text
- `is_verified` — email verification gate before checkout
- `is_active` — soft-disable without deleting history

### SW_USER_ADDRESS
Stores multiple saved addresses per user (home, work, etc.).
- `is_default` — the pre-selected address at checkout
- `country` — ISO 3166-1 alpha-2 (e.g. US, GB)
- Address is referenced at order time but the order snapshots it into `SW_ORDER.shipping_address` (JSONB) so future edits don't corrupt history

---

## Catalog

### SW_CATEGORY
Self-referencing tree via `parent_id`. Supports unlimited depth (Electronics → Laptops → Gaming Laptops).
- `slug` — URL-safe identifier for routing (`/c/electronics/laptops`)
- `sort_order` — controls display order in nav/sidebar
- Leaf categories are assigned to products; branch categories are navigation only

### SW_BRAND
Simple brand/manufacturer registry.
- Linked to products for brand-filter facet on search results
- `slug` — used in brand landing page URLs

### SW_PRODUCT
The central catalog record. One row = one logical product (e.g. "Sony WH-1000XM5 Headphones").
- `base_price` / `sale_price` — sale_price NULL means no active discount; UI shows base_price
- `bullet_points` — PostgreSQL `TEXT[]` array for the feature list shown on PDP
- `badge` — free-text label rendered on card (Best Seller, Deal, New, etc.)
- `avg_rating` / `review_count` — denormalized for fast listing queries; updated by trigger or background job when SW_REVIEW changes
- `is_prime` — marks free/fast delivery eligibility
- `is_featured` — surfaces product in homepage/hero slots
- Full-text search index (`GIN`) on `title` enables `to_tsvector` queries without a separate search engine in MVP

### SW_PRODUCT_IMAGE
Multiple images per product, ordered by `sort_order`.
- `is_primary` — the thumbnail shown on listing cards and OG image
- Designed to hold CDN URLs; actual file storage is external (S3, CloudFront, etc.)

### SW_PRODUCT_VARIANT
SKU-level child of a product. A product must have at least one variant even if there are no options (single-variant products).
- `sku` — unique warehouse/barcode identifier
- `price` — can override the parent product price per variant (e.g. larger storage = higher price)
- `stock_qty` — kept in sync with SW_INVENTORY; this column is the fast-read cache

### SW_PRODUCT_ATTRIBUTE
Attribute name registry (Color, Size, Storage, Material, etc.).
- Shared across all products; not per-product to avoid duplication

### SW_VARIANT_ATTRIBUTE_VALUE
Junction table linking a variant to its attribute values.
- Composite PK `(variant_id, attribute_id)` — one value per attribute per variant
- Example: variant "Red / XL" → two rows: (Color=Red), (Size=XL)
- Used to render the variant selector UI on the product detail page

---

## Inventory

### SW_INVENTORY
Authoritative stock record per variant (one-to-one with SW_PRODUCT_VARIANT).
- `qty_available` — units that can be sold right now
- `qty_reserved` — units held in open carts/pending orders; not yet deducted
- `reorder_level` — threshold for low-stock alerts to the seller/warehouse
- `SW_PRODUCT_VARIANT.stock_qty` is a denormalized copy for fast reads; SW_INVENTORY is the source of truth for reservation logic

---

## Reviews & Ratings

### SW_REVIEW
One review per user per product (enforced by UNIQUE constraint).
- `is_verified` — TRUE only if the user has a delivered order containing this product
- `helpful_count` — upvote count ("Was this review helpful?")
- When a review is inserted/updated, a background job or trigger should recalculate `SW_PRODUCT.avg_rating` and `review_count`

---

## Cart

### SW_CART
One cart per logged-in user OR per guest session.
- `user_id` — set for authenticated users; NULL for guests
- `session_id` — set for guests; NULL for authenticated users
- CHECK constraint ensures at least one of the two is populated
- On login, guest cart should be merged into the user cart (application logic)

### SW_CART_ITEM
Line items inside a cart. Keyed on `(cart_id, variant_id)` — adding the same variant again increments `quantity`.
- Quantity is validated > 0 at DB level
- Stock availability is checked at checkout time, not at add-to-cart time (optimistic UX)

---

## Orders

### SW_ORDER
Immutable record of a placed order.
- `shipping_address` — JSONB snapshot of the address at order time; decoupled from SW_USER_ADDRESS so users can edit/delete addresses freely
- `status` — ENUM: `pending → confirmed → processing → shipped → delivered` or `cancelled / refunded`
- Financial columns (`subtotal`, `shipping_cost`, `tax_amount`, `discount_amount`, `total_amount`) are all stored as snapshots; never recalculated from live prices

### SW_ORDER_ITEM
Line items for an order. Product/variant titles are snapshotted as text columns.
- `unit_price` and `line_total` are locked at order time; catalog price changes don't affect past orders
- `variant_id` FK is kept for reference/reporting but the display data comes from the snapshot columns

---

## Payments

### SW_PAYMENT
Provider-agnostic payment record linked to an order.
- `provider` — e.g. `stripe`, `paypal`, `apple_pay`
- `provider_ref` — the external transaction/charge ID for reconciliation and refund calls
- `status` ENUM: `pending → authorized → captured` or `failed / refunded`
- One order can have multiple payment rows (e.g. partial refund creates a new `refunded` row)

---

## Shipping

### SW_SHIPMENT
Tracks physical shipment of an order.
- One order can have multiple shipments (split fulfillment)
- `tracking_number` — passed to carrier API or displayed to customer
- `delivered_at` — when populated, triggers order status update to `delivered`

---

## Promotions

### SW_COUPON
Discount codes redeemable at checkout.
- `discount_type` ENUM: `percentage` (e.g. 20% off), `fixed_amount` (e.g. $10 off), `free_shipping`
- `min_order_value` — minimum cart subtotal required to apply
- `max_uses` — NULL means unlimited; `used_count` is incremented on successful order
- `valid_from` / `valid_until` — time-bounded campaigns

---

## Search & Wishlist

### SW_WISHLIST
Saved items per user. Many-to-many between SW_USER and SW_PRODUCT_VARIANT.
- UNIQUE `(user_id, variant_id)` prevents duplicates
- Used to power "Save for Later" and wishlist sharing features

### SW_SEARCH_LOG
Analytics table capturing every search query.
- `user_id` — NULL for guest searches (SET NULL on user delete)
- `result_count` — recorded so zero-result queries can be identified and acted on (add products, fix synonyms)
- Feed this table into a BI tool or use it to power "trending searches" and autocomplete suggestions

---

## Cross-Cutting Concerns

| Concern | Approach |
|---|---|
| PKs | UUID (`gen_random_uuid()`) for user-facing entities; SERIAL for internal/lookup tables |
| Soft deletes | `is_active` flag on USER, PRODUCT, CATEGORY, BRAND — rows are never hard-deleted |
| Price snapshots | ORDER, ORDER_ITEM store prices at transaction time; catalog changes are non-destructive |
| Address snapshots | ORDER stores address as JSONB; SW_USER_ADDRESS can be freely edited |
| Full-text search | GIN index on `SW_PRODUCT.title` via `to_tsvector('english', title)` |
| Auto timestamps | `sw_set_updated_at()` trigger on SW_USER, SW_PRODUCT, SW_ORDER, SW_CART |
| Enum types | `sw_order_status`, `sw_payment_status`, `sw_discount_type` — constrained at DB level |
| Guest support | SW_CART supports both `user_id` and `session_id` for pre-login shopping |

---

## Planned / Future Tables (MVP2+)

| Table | Purpose |
|---|---|
| SW_SELLER_PROFILE | Extended seller info, ratings, payout details |
| SW_PRODUCT_QUESTION | Q&A section on product detail page |
| SW_NOTIFICATION | In-app / email notification queue |
| SW_RETURN_REQUEST | RMA workflow for returns and refunds |
| SW_COUPON_USAGE | Per-user coupon redemption log |
| SW_RECOMMENDATION | Personalized product recommendation store |
