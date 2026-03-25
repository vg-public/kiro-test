# ShopWire — Product Requirements Document (PRD)

**Project:** ShopWire eCommerce Platform  
**Inspired by:** Amazon.com  
**Status:** In Progress — MVP1 Complete  
**Last Updated:** 2026-03-25  
**Workspace layout:** `front-end/` · `back-end/` · `work/` (design artefacts)

---

## 1. Project Vision

ShopWire is a full-stack eCommerce platform modelled after Amazon.com. The goal is to build a scalable, multi-seller marketplace where buyers can discover, evaluate, and purchase products across multiple categories, and sellers can list and manage their inventory.

The project is being built incrementally across MVPs. All design artefacts, wireframes, and schema live in the `work/` folder until they graduate into `front-end/` and `back-end/` implementations.

---

## 2. Workspace Structure

```
/
├── front-end/          # React (or chosen framework) application — to be built
├── back-end/           # API server — to be built
├── work/               # Design artefacts (current working folder)
│   ├── index.html      # MVP1 HTML wireframe
│   ├── style.css       # MVP1 wireframe styles
│   ├── app.js          # MVP1 wireframe JS (mock data + search)
│   ├── schema.sql      # PostgreSQL DDL — all SW_ tables
│   ├── schema_def.md   # Table-by-table purpose reference
│   └── shop_wire-prd.md  ← this file
├── README.md
└── LICENSE
```

---

## 3. MVP Roadmap

| MVP | Scope | Status |
|-----|-------|--------|
| MVP1 | Product listing page + search (wireframe) | ✅ Complete |
| MVP2 | Product detail page, variant selector, reviews | Planned |
| MVP3 | User auth, registration, email verification | Planned |
| MVP4 | Cart, checkout, address management | Planned |
| MVP5 | Orders, payments, order history | Planned |
| MVP6 | Seller portal — list/manage products, inventory | Planned |
| MVP7 | Promotions, coupons, wishlist | Planned |
| MVP8 | Search improvements, recommendations, analytics | Planned |

---

## 4. MVP1 — Completed Work

### 4.1 Wireframe (work/index.html + style.css + app.js)

A fully functional static wireframe that demonstrates the core shopping UI.

**Pages / Sections built:**
- Header — logo, category-scoped search bar, sign-in link, cart counter
- Sub-navigation — category quick links
- Hero banner — promotional area
- Sidebar filters — department checkboxes, price range radios, star rating, availability
- Product listing grid — responsive, auto-fill columns
- Sort bar — sort dropdown, result count, grid/list view toggle
- Pagination controls
- Footer — four-column links (About, Sell, Payments, Help)

**Product card contains:**
- Product image placeholder (emoji for wireframe)
- Badge (Best Seller / Deal)
- Title (2-line clamp)
- Star rating + review count
- Current price + original price + discount %
- Prime delivery indicator
- Add to Cart button with visual feedback

**Search behaviour (client-side, mock data):**
- Filters by product title or category (case-insensitive substring match)
- Enter key and button both trigger search
- Zero-results state handled with friendly message
- Result count updates dynamically

**Mock product categories used:**
Electronics, Clothing, Books, Home & Garden, Sports, Health

**Cart (wireframe only):**
- Counter in header increments on Add to Cart
- Button shows "Added ✓" feedback for 1.2s
- No persistence — resets on page reload

**Product click:**
- Alert placeholder — "Detail page coming in MVP2"

---

## 5. Technical Design Decisions

### 5.1 Frontend

| Decision | Choice | Reason |
|----------|--------|--------|
| Wireframe tech | Vanilla HTML/CSS/JS | Zero dependencies, fast to iterate, easy to hand off to any framework |
| Layout | CSS Grid (auto-fill, minmax 200px) | Naturally responsive without media query breakpoints per column |
| Color palette | Amazon-inspired — `#131921` header, `#ff9900` accent, `#ffd814` CTA | Familiar eCommerce visual language |
| Search (MVP1) | Client-side substring filter on mock array | No backend needed for wireframe validation |
| Search (MVP2+) | PostgreSQL `to_tsvector` GIN index → REST API | Avoids external search engine dependency in early MVPs |
| Responsive | Single breakpoint at 768px — sidebar stacks below content | Covers mobile without over-engineering |

**Planned front-end stack (front-end/ folder):**
- Framework: TBD (React / Next.js recommended for SSR + SEO)
- State management: TBD (Zustand / Redux Toolkit)
- Styling: TBD (Tailwind CSS / CSS Modules)
- API client: Fetch / Axios with React Query for caching

### 5.2 Backend

**Planned back-end stack (back-end/ folder):**
- Runtime: Node.js (Express or Fastify) — or Python (FastAPI) — TBD
- Database: PostgreSQL (decided)
- ORM / Query builder: TBD (Prisma / Drizzle / Knex)
- Auth: JWT access tokens + refresh tokens; bcrypt/argon2 for password hashing
- File storage: External CDN (S3-compatible) for product images
- Payment: Provider-agnostic abstraction layer (Stripe recommended for MVP5)

### 5.3 Database

**Engine:** PostgreSQL  
**Table prefix:** `SW_` (all tables)  
**DDL file:** `work/schema.sql`  
**Reference:** `work/schema_def.md`

#### Entity Groups

| Group | Tables |
|-------|--------|
| Users & Auth | SW_USER, SW_USER_ADDRESS |
| Catalog | SW_CATEGORY, SW_BRAND, SW_PRODUCT, SW_PRODUCT_IMAGE, SW_PRODUCT_VARIANT, SW_PRODUCT_ATTRIBUTE, SW_VARIANT_ATTRIBUTE_VALUE |
| Inventory | SW_INVENTORY |
| Reviews | SW_REVIEW |
| Cart | SW_CART, SW_CART_ITEM |
| Orders | SW_ORDER, SW_ORDER_ITEM |
| Payments | SW_PAYMENT |
| Shipping | SW_SHIPMENT |
| Promotions | SW_COUPON |
| Search & Wishlist | SW_WISHLIST, SW_SEARCH_LOG |

#### Key Design Decisions

**UUIDs for user-facing PKs** — SW_USER, SW_PRODUCT, SW_ORDER, SW_CART, SW_PAYMENT, SW_SHIPMENT, SW_REVIEW, SW_PRODUCT_VARIANT all use `gen_random_uuid()`. Internal/lookup tables (SW_CATEGORY, SW_BRAND, etc.) use SERIAL.

**Soft deletes** — `is_active` flag on SW_USER, SW_PRODUCT, SW_CATEGORY, SW_BRAND. Rows are never hard-deleted to preserve referential integrity and audit history.

**Price & address snapshots** — SW_ORDER stores `shipping_address` as JSONB and all financial totals as fixed columns. SW_ORDER_ITEM stores `unit_price`, `line_total`, `product_title`, `variant_title` as snapshots. This means catalog price changes and address edits never corrupt historical orders.

**Product variants model** — Every product has one or more variants (SW_PRODUCT_VARIANT). Even a product with no options (e.g. a book) has a single default variant. This keeps cart and order line items consistently keyed on `variant_id`.

**Attribute-value EAV for variants** — SW_PRODUCT_ATTRIBUTE + SW_VARIANT_ATTRIBUTE_VALUE allow arbitrary variant dimensions (Color, Size, Storage, Material) without schema changes per product type.

**Denormalized rating cache** — `SW_PRODUCT.avg_rating` and `review_count` are denormalized for fast listing queries. A trigger or background job recalculates them when SW_REVIEW changes.

**Inventory dual-write** — `SW_PRODUCT_VARIANT.stock_qty` is a fast-read cache. `SW_INVENTORY` is the authoritative source with `qty_available` and `qty_reserved` for reservation logic during checkout.

**Guest cart support** — SW_CART accepts either `user_id` (authenticated) or `session_id` (guest). On login, application logic merges the guest cart into the user cart.

**Full-text search** — GIN index on `SW_PRODUCT.title` using `to_tsvector('english', title)`. Sufficient for MVP; can be replaced with Elasticsearch/OpenSearch in later MVPs without schema changes.

**Payment abstraction** — SW_PAYMENT stores `provider` (stripe, paypal, apple_pay) and `provider_ref` (external transaction ID). The schema is provider-agnostic so the payment provider can be swapped or multiple providers supported simultaneously.

**Enum types** — `sw_order_status`, `sw_payment_status`, `sw_discount_type` are PostgreSQL ENUM types. Values are constrained at the DB level, not just application level.

**Auto-updated timestamps** — `sw_set_updated_at()` PL/pgSQL trigger fires BEFORE UPDATE on SW_USER, SW_PRODUCT, SW_ORDER, SW_CART.

---

## 6. Feature Requirements by MVP

### MVP2 — Product Detail Page
- Product image gallery (primary + thumbnails)
- Variant selector (color swatches, size buttons, dropdown)
- Price display — base vs sale price, discount badge
- Prime delivery eligibility + estimated delivery date
- Add to Cart / Buy Now buttons
- Product description + bullet points
- Customer reviews section — star breakdown, individual reviews, helpful votes
- "Customers also viewed" placeholder section

### MVP3 — User Authentication
- Register with email + password
- Email verification flow
- Login / logout
- Password reset via email
- JWT access token (short-lived) + refresh token (httpOnly cookie)
- Protected routes — cart, checkout, orders, wishlist require auth

### MVP4 — Cart & Checkout
- Persistent cart (DB-backed, synced across devices)
- Guest cart with merge on login
- Quantity update and item removal
- Coupon code input
- Address selection / add new address
- Order summary with subtotal, shipping, tax, discount, total
- Stock check at checkout time (not at add-to-cart)

### MVP5 — Orders & Payments
- Place order — creates SW_ORDER + SW_ORDER_ITEM records
- Payment integration (Stripe recommended)
- Order confirmation page + email
- Order history page
- Order detail page with shipment tracking
- Order status lifecycle: pending → confirmed → processing → shipped → delivered

### MVP6 — Seller Portal
- Seller registration / profile (SW_SELLER_PROFILE — MVP2+ table)
- Product listing creation — title, description, images, variants, pricing
- Inventory management — stock levels, reorder alerts
- Order management — view and update fulfillment status
- Basic sales dashboard

### MVP7 — Promotions & Wishlist
- Coupon creation and management (SW_COUPON)
- Coupon redemption at checkout
- Wishlist — add/remove items, view saved items
- "Save for Later" from cart

### MVP8 — Search & Recommendations
- Autocomplete suggestions powered by SW_SEARCH_LOG
- Trending searches
- Zero-result query reporting
- Personalized recommendations (SW_RECOMMENDATION — future table)
- Advanced filters — brand, attribute values (color, size), Prime only, in-stock only

---

## 7. Non-Functional Requirements

| Concern | Requirement |
|---------|-------------|
| Security | Passwords hashed with bcrypt/argon2; JWT with short expiry; HTTPS only; parameterised queries (no raw SQL interpolation) |
| Performance | Product listing page < 200ms API response; GIN FTS index for search; denormalized rating/review counts |
| Scalability | Stateless API (JWT); DB connection pooling; image assets on CDN |
| Data integrity | Soft deletes; price/address snapshots; DB-level ENUM constraints; CHECK constraints on prices and quantities |
| Auditability | `created_at` / `updated_at` on all major tables; SW_SEARCH_LOG for query analytics |
| Availability | Guest checkout supported (no forced registration); cart persists across sessions |

---

## 8. Open Decisions (to resolve in future sessions)

| # | Decision | Options | Notes |
|---|----------|---------|-------|
| 1 | Frontend framework | React + Next.js / Vue + Nuxt / plain React | Next.js preferred for SSR/SEO |
| 2 | Backend language/framework | Node.js + Fastify / Python + FastAPI | TBD |
| 3 | ORM / query builder | Prisma / Drizzle / Knex (Node) or SQLAlchemy (Python) | TBD |
| 4 | Payment provider | Stripe (recommended) / PayPal / Braintree | Schema is provider-agnostic |
| 5 | Image storage | AWS S3 + CloudFront / Cloudflare R2 / Supabase Storage | CDN URLs stored in SW_PRODUCT_IMAGE |
| 6 | Deployment | Docker Compose (dev) / ECS / Railway / Render | TBD |
| 7 | Search upgrade path | Stay on PostgreSQL FTS / migrate to Elasticsearch | Revisit at MVP8 based on data volume |
| 8 | Email service | SendGrid / Resend / SES | Needed for MVP3 email verification |

---

## 9. File Reference

| File | Purpose |
|------|---------|
| `work/index.html` | MVP1 wireframe — full page layout |
| `work/style.css` | MVP1 wireframe styles — Amazon-inspired theme |
| `work/app.js` | MVP1 wireframe JS — mock data, search, cart counter |
| `work/schema.sql` | PostgreSQL DDL — all 20 SW_ tables, indexes, triggers |
| `work/schema_def.md` | Table-by-table purpose, column intentions, design rationale |
| `work/shop_wire-prd.md` | This file — full project PRD and context |
