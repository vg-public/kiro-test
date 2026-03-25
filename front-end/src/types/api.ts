// ── Pagination ──────────────────────────────────────────────
export interface Pagination {
  page: number
  limit: number
  total: number
  pages: number
}

// ── Error ────────────────────────────────────────────────────
export interface ApiError {
  code: string
  message: string
  details?: Record<string, unknown>
}

// ── Auth ─────────────────────────────────────────────────────
export interface RegisterRequest {
  email: string
  password: string
  first_name: string
  last_name: string
  phone?: string
}

export interface LoginRequest {
  email: string
  password: string
}

export interface AuthResponse {
  access_token: string
  token_type: string
  expires_in: number
  user: UserProfile
}

// ── User ─────────────────────────────────────────────────────
export interface UserProfile {
  user_id: string
  email: string
  first_name: string
  last_name: string
  phone: string | null
  is_verified: boolean
  created_at: string
}

// ── Address ──────────────────────────────────────────────────
export interface Address {
  address_id: string
  label: string
  full_name: string
  line1: string
  line2: string | null
  city: string
  state: string
  postal_code: string
  country: string
  is_default: boolean
}

export interface AddressRequest {
  label?: string
  full_name: string
  line1: string
  line2?: string
  city: string
  state: string
  postal_code: string
  country: string
  is_default?: boolean
}

// ── Category ─────────────────────────────────────────────────
export interface Category {
  category_id: number
  parent_id: number | null
  name: string
  slug: string
  image_url: string | null
  children?: Category[]
}

// ── Product ──────────────────────────────────────────────────
export interface ProductSummary {
  product_id: string
  title: string
  slug: string
  brand: string | null
  category: string
  primary_image: string | null
  base_price: number
  sale_price: number | null
  currency: string
  discount_pct: number | null
  badge: string | null
  avg_rating: number
  review_count: number
  is_prime: boolean
  in_stock: boolean
}

export interface ProductImage {
  url: string
  alt_text: string
  is_primary: boolean
}

export interface VariantAttribute {
  name: string
  value: string
}

export interface Variant {
  variant_id: string
  sku: string
  title: string
  price: number
  stock_qty: number
  is_active: boolean
  attributes: VariantAttribute[]
}

export interface ProductDetail extends ProductSummary {
  description: string | null
  bullet_points: string[]
  images: ProductImage[]
  variants: Variant[]
}

// ── Review ───────────────────────────────────────────────────
export interface Review {
  review_id: string
  user_name: string
  rating: number
  title: string | null
  body: string | null
  is_verified: boolean
  helpful_count: number
  created_at: string
}

export interface ReviewRequest {
  rating: number
  title?: string
  body?: string
}

export interface ReviewsResponse {
  pagination: Pagination
  rating_breakdown: {
    5: number
    4: number
    3: number
    2: number
    1: number
  }
  reviews: Review[]
}

// ── Cart ─────────────────────────────────────────────────────
export interface CartItem {
  cart_item_id: number
  variant_id: string
  product_id: string
  title: string
  variant_title: string
  image_url: string | null
  unit_price: number
  quantity: number
  line_total: number
  is_prime: boolean
}

export interface Cart {
  cart_id: string
  items: CartItem[]
  subtotal: number
  item_count: number
}

export interface AddToCartRequest {
  variant_id: string
  quantity: number
}

export interface UpdateCartItemRequest {
  quantity: number
}

export interface MergeCartRequest {
  session_id: string
}

// ── Order ────────────────────────────────────────────────────
export type OrderStatus =
  | 'pending'
  | 'confirmed'
  | 'processing'
  | 'shipped'
  | 'delivered'
  | 'cancelled'
  | 'refunded'

export interface OrderSummary {
  order_id: string
  status: OrderStatus
  total_amount: number
  currency: string
  item_count: number
  created_at: string
}

export interface OrderItem {
  order_item_id: number
  product_title: string
  variant_title: string
  unit_price: number
  quantity: number
  line_total: number
}

export interface Shipment {
  shipment_id: string
  carrier: string | null
  tracking_number: string | null
  shipped_at: string | null
  estimated_at: string | null
  delivered_at: string | null
}

export interface OrderDetail extends OrderSummary {
  shipping_address: Address
  subtotal: number
  shipping_cost: number
  tax_amount: number
  discount_amount: number
  notes: string | null
  items: OrderItem[]
  shipments: Shipment[]
}

export interface PlaceOrderRequest {
  address_id: string
  coupon_code?: string
  notes?: string
}

export interface OrdersResponse {
  pagination: Pagination
  orders: OrderSummary[]
}

// ── Coupon ───────────────────────────────────────────────────
export interface CouponValidateRequest {
  code: string
  cart_total?: number
}

export interface CouponValidateResponse {
  valid: boolean
  discount_type: 'percentage' | 'fixed_amount' | 'free_shipping'
  discount_value: number
  message: string
}

// ── Wishlist ─────────────────────────────────────────────────
export interface WishlistItem {
  wishlist_id: number
  product: ProductSummary
  variant_id: string
  added_at: string
}

// ── Search ───────────────────────────────────────────────────
export interface SearchFacetItem {
  name: string
  count: number
}

export interface SearchResponse {
  query: string
  pagination: Pagination
  results: ProductSummary[]
  facets: {
    categories: SearchFacetItem[]
    brands: SearchFacetItem[]
    price_range: { min: number; max: number }
  }
}

export interface SuggestionsResponse {
  suggestions: string[]
}

// ── Product list params ───────────────────────────────────────
export interface ProductListParams {
  page?: number
  limit?: number
  category?: string
  brand?: string
  price_min?: number
  price_max?: number
  rating?: number
  prime?: boolean
  in_stock?: boolean
  badge?: string
  sort?: 'featured' | 'price_asc' | 'price_desc' | 'rating' | 'newest'
}

export interface SearchParams {
  q: string
  page?: number
  limit?: number
  category?: string
  price_min?: number
  price_max?: number
  sort?: 'relevance' | 'price_asc' | 'price_desc' | 'rating' | 'newest'
}
