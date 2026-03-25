# ShopWire

A full-stack eCommerce platform inspired by Amazon.com. Built incrementally across MVPs — from wireframe to production-ready application.

## Project Structure

```
/
├── front-end/          # Frontend application (React/Next.js — MVP3+)
├── back-end/           # API server (Node.js/Python — MVP3+)
├── work/               # Design artefacts, wireframes, DB scripts
│   ├── index.html          # MVP1 HTML wireframe
│   ├── style.css           # MVP1 wireframe styles
│   ├── app.js              # MVP1 wireframe JS (mock data + search)
│   ├── schema.sql          # PostgreSQL DDL — all SW_ tables
│   ├── schema_dml.sql      # Sample INSERT data (12 wireframe products)
│   ├── schema_rollback.sql # DELETE sample data (reverse FK order)
│   ├── schema_drop.sql     # DROP all tables, types, functions
│   ├── schema_def.md       # Table-by-table purpose and design notes
│   └── shop_wire-prd.md    # Full PRD — scope, decisions, MVP roadmap
└── README.md
```

## MVP Status

| MVP | Scope | Status |
|-----|-------|--------|
| MVP1 | Product listing + search wireframe | ✅ Complete |
| MVP2 | Product detail page, variants, reviews | Planned |
| MVP3 | User auth (register, login, JWT) | Planned |
| MVP4 | Cart, checkout, address management | Planned |
| MVP5 | Orders, payments, order history | Planned |
| MVP6 | Seller portal — listings, inventory | Planned |
| MVP7 | Promotions, coupons, wishlist | Planned |
| MVP8 | Search improvements, recommendations | Planned |

## Wireframe (MVP1)

Open `work/index.html` in a browser — no build step needed.

Features working in the wireframe:
- Header with category-scoped search bar and cart counter
- Sidebar filters (department, price range, rating, availability)
- Responsive product grid (12 mock products)
- Live search by title or category (Enter key or button)
- Add to Cart with visual feedback
- Sort controls and pagination (UI only)

## Database

**Engine:** PostgreSQL  
**Table prefix:** `SW_` (20 tables total)

### Reset & Seed

```bash
psql -d your_db -f work/schema_drop.sql     # drop everything
psql -d your_db -f work/schema.sql          # create schema
psql -d your_db -f work/schema_dml.sql      # insert sample data
```

### Remove sample data only

```bash
psql -d your_db -f work/schema_rollback.sql
```

### Schema groups

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

See `work/schema_def.md` for full table-by-table documentation.

## Tech Stack (Planned)

| Layer | Technology |
|-------|-----------|
| Frontend | React / Next.js (TBD) |
| Backend | Node.js + Fastify or Python + FastAPI (TBD) |
| Database | PostgreSQL |
| Auth | JWT + refresh tokens, bcrypt/argon2 |
| Payments | Stripe (provider-agnostic schema) |
| Images | CDN / S3-compatible storage |

## Reference Docs

- `work/shop_wire-prd.md` — full PRD with all design decisions and open questions
- `work/schema_def.md` — database schema reference
