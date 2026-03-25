# ShopWire — React Front-End

React 18 + Vite + TypeScript front-end for the ShopWire eCommerce platform.
Consumes the REST API defined in `work/api_contract.yaml`.

## Prerequisites

| Tool | Version |
|------|---------|
| Node.js | 18+ |
| npm | 9+ |

## Setup

```bash
cd front-end
npm install
```

## Running the Dev Server

```bash
npm run dev
# → http://localhost:5173
```

The back-end API must be running on `http://localhost:4000/v1`.
See `back-end/README.md` for setup instructions.

## Running Tests

```bash
npm test
```

Runs all tests once via Vitest (`--run` mode). Coverage report printed to terminal.

Watch mode:
```bash
npm run test:watch
```

## Linting

```bash
npm run lint
```

## Building for Production

```bash
npm run build
# output → dist/
```

## Project Structure

```
src/
├── api/
│   └── shopwireApi.ts       RTK Query — all API endpoints
├── store/
│   ├── index.ts             Redux store
│   ├── authSlice.ts         JWT token + user profile state
│   └── cartSlice.ts         Cart count + drawer open/close
├── types/
│   └── api.ts               TypeScript interfaces (matches API contract)
├── components/
│   ├── layout/              Header, SubNav, Footer
│   ├── product/             ProductCard, ProductGrid, ProductDetail
│   ├── search/              SearchBar, SearchResults
│   ├── filters/             SidebarFilters
│   ├── cart/                CartDrawer, CartItem
│   ├── auth/                LoginForm, RegisterForm
│   └── common/              StarRating, Pagination, ErrorMessage
├── pages/
│   ├── HomePage.tsx
│   ├── ProductListPage.tsx
│   ├── ProductDetailPage.tsx
│   ├── SearchPage.tsx
│   ├── CartPage.tsx
│   ├── CheckoutPage.tsx
│   ├── OrdersPage.tsx
│   └── AuthPage.tsx
├── styles/
│   └── global.css           Amazon-inspired theme (ported from wireframe)
└── __tests__/
    ├── components/          Header, ProductCard, SearchBar, SidebarFilters, StarRating
    ├── pages/               HomePage, ProductListPage
    └── store/               authSlice, cartSlice
```

## Routes

| Path | Page |
|------|------|
| `/` | HomePage |
| `/products` | ProductListPage |
| `/products/:productId` | ProductDetailPage |
| `/search` | SearchPage |
| `/cart` | CartPage |
| `/checkout` | CheckoutPage |
| `/orders` | OrdersPage |
| `/auth` | AuthPage |

## State Management

- `auth` slice — stores JWT access token (in-memory, not localStorage) and user profile
- `cart` slice — stores item count and cart drawer open/close state
- RTK Query — handles all API calls with automatic caching, loading, and error states
- JWT token is injected into every API request via `prepareHeaders`
- On 401, the app dispatches `logout()` and redirects to `/auth`

## Design

Follows the wireframe in `work/index.html`:
- Header: `#131921` background, `#ff9900` logo and search button
- SubNav: `#232f3e` background
- Product cards: white, border, hover shadow, `#ffd814` Add to Cart button
- Responsive: sidebar stacks below content at 768px breakpoint
