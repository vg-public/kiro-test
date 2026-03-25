import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react'
import type { RootState } from '../store'
import type {
  AuthResponse,
  LoginRequest,
  RegisterRequest,
  UserProfile,
  Address,
  AddressRequest,
  Category,
  ProductSummary,
  ProductDetail,
  Review,
  ReviewRequest,
  ReviewsResponse,
  Cart,
  AddToCartRequest,
  UpdateCartItemRequest,
  MergeCartRequest,
  OrderDetail,
  OrdersResponse,
  PlaceOrderRequest,
  CouponValidateRequest,
  CouponValidateResponse,
  WishlistItem,
  SearchResponse,
  SuggestionsResponse,
  ProductListParams,
  SearchParams,
} from '../types/api'

export const shopwireApi = createApi({
  reducerPath: 'shopwireApi',
  baseQuery: fetchBaseQuery({
    baseUrl: 'http://localhost:4000/v1',
    credentials: 'include',
    prepareHeaders: (headers, { getState }) => {
      const token = (getState() as RootState).auth.token
      if (token) {
        headers.set('Authorization', `Bearer ${token}`)
      }
      return headers
    },
  }),
  tagTypes: ['Cart', 'Wishlist', 'Orders', 'Profile', 'Addresses'],
  endpoints: (builder) => ({
    // ── Auth ──────────────────────────────────────────────
    login: builder.mutation<AuthResponse, LoginRequest>({
      query: (body) => ({ url: '/auth/login', method: 'POST', body }),
    }),
    register: builder.mutation<AuthResponse, RegisterRequest>({
      query: (body) => ({ url: '/auth/register', method: 'POST', body }),
    }),
    refreshToken: builder.mutation<AuthResponse, void>({
      query: () => ({ url: '/auth/refresh', method: 'POST' }),
    }),

    // ── User ──────────────────────────────────────────────
    getMe: builder.query<UserProfile, void>({
      query: () => '/users/me',
      providesTags: ['Profile'],
    }),
    updateMe: builder.mutation<UserProfile, Partial<UserProfile>>({
      query: (body) => ({ url: '/users/me', method: 'PATCH', body }),
      invalidatesTags: ['Profile'],
    }),
    getAddresses: builder.query<Address[], void>({
      query: () => '/users/me/addresses',
      providesTags: ['Addresses'],
    }),
    addAddress: builder.mutation<Address, AddressRequest>({
      query: (body) => ({ url: '/users/me/addresses', method: 'POST', body }),
      invalidatesTags: ['Addresses'],
    }),

    // ── Categories ────────────────────────────────────────
    getCategories: builder.query<Category[], void>({
      query: () => '/categories',
    }),

    // ── Products ──────────────────────────────────────────
    getProducts: builder.query<SearchResponse, ProductListParams>({
      query: (params) => ({
        url: '/products',
        params: params as Record<string, unknown>,
      }),
    }),
    getProduct: builder.query<ProductDetail, string>({
      query: (productId) => `/products/${productId}`,
    }),
    getProductReviews: builder.query<
      ReviewsResponse,
      { productId: string; page?: number; limit?: number; sort?: string }
    >({
      query: ({ productId, ...params }) => ({
        url: `/products/${productId}/reviews`,
        params: params as Record<string, unknown>,
      }),
    }),
    submitReview: builder.mutation<
      Review,
      { productId: string; body: ReviewRequest }
    >({
      query: ({ productId, body }) => ({
        url: `/products/${productId}/reviews`,
        method: 'POST',
        body,
      }),
    }),

    // ── Search ────────────────────────────────────────────
    searchProducts: builder.query<SearchResponse, SearchParams>({
      query: (params) => ({
        url: '/search',
        params: params as Record<string, unknown>,
      }),
    }),
    getSearchSuggestions: builder.query<SuggestionsResponse, string>({
      query: (q) => ({ url: '/search/suggestions', params: { q } }),
    }),

    // ── Cart ──────────────────────────────────────────────
    getCart: builder.query<Cart, void>({
      query: () => '/cart',
      providesTags: ['Cart'],
    }),
    addToCart: builder.mutation<Cart, AddToCartRequest>({
      query: (body) => ({ url: '/cart', method: 'POST', body }),
      invalidatesTags: ['Cart'],
    }),
    updateCartItem: builder.mutation<
      Cart,
      { cartItemId: number; body: UpdateCartItemRequest }
    >({
      query: ({ cartItemId, body }) => ({
        url: `/cart/items/${cartItemId}`,
        method: 'PATCH',
        body,
      }),
      invalidatesTags: ['Cart'],
    }),
    removeCartItem: builder.mutation<Cart, number>({
      query: (cartItemId) => ({
        url: `/cart/items/${cartItemId}`,
        method: 'DELETE',
      }),
      invalidatesTags: ['Cart'],
    }),
    mergeCart: builder.mutation<Cart, MergeCartRequest>({
      query: (body) => ({ url: '/cart/merge', method: 'POST', body }),
      invalidatesTags: ['Cart'],
    }),

    // ── Orders ────────────────────────────────────────────
    getOrders: builder.query<
      OrdersResponse,
      { page?: number; limit?: number; status?: string }
    >({
      query: (params) => ({
        url: '/orders',
        params: params as Record<string, unknown>,
      }),
      providesTags: ['Orders'],
    }),
    getOrder: builder.query<OrderDetail, string>({
      query: (orderId) => `/orders/${orderId}`,
    }),
    placeOrder: builder.mutation<OrderDetail, PlaceOrderRequest>({
      query: (body) => ({ url: '/orders', method: 'POST', body }),
      invalidatesTags: ['Orders', 'Cart'],
    }),
    cancelOrder: builder.mutation<OrderDetail, string>({
      query: (orderId) => ({
        url: `/orders/${orderId}/cancel`,
        method: 'POST',
      }),
      invalidatesTags: ['Orders'],
    }),

    // ── Coupons ───────────────────────────────────────────
    validateCoupon: builder.mutation<
      CouponValidateResponse,
      CouponValidateRequest
    >({
      query: (body) => ({ url: '/coupons/validate', method: 'POST', body }),
    }),

    // ── Wishlist ──────────────────────────────────────────
    getWishlist: builder.query<WishlistItem[], void>({
      query: () => '/wishlist',
      providesTags: ['Wishlist'],
    }),
    addToWishlist: builder.mutation<WishlistItem, { variant_id: string }>({
      query: (body) => ({ url: '/wishlist', method: 'POST', body }),
      invalidatesTags: ['Wishlist'],
    }),
    removeFromWishlist: builder.mutation<void, string>({
      query: (variantId) => ({
        url: `/wishlist/${variantId}`,
        method: 'DELETE',
      }),
      invalidatesTags: ['Wishlist'],
    }),
  }),
})

export const {
  useLoginMutation,
  useRegisterMutation,
  useRefreshTokenMutation,
  useGetMeQuery,
  useUpdateMeMutation,
  useGetAddressesQuery,
  useAddAddressMutation,
  useGetCategoriesQuery,
  useGetProductsQuery,
  useGetProductQuery,
  useGetProductReviewsQuery,
  useSubmitReviewMutation,
  useSearchProductsQuery,
  useGetSearchSuggestionsQuery,
  useGetCartQuery,
  useAddToCartMutation,
  useUpdateCartItemMutation,
  useRemoveCartItemMutation,
  useMergeCartMutation,
  useGetOrdersQuery,
  useGetOrderQuery,
  usePlaceOrderMutation,
  useCancelOrderMutation,
  useValidateCouponMutation,
  useGetWishlistQuery,
  useAddToWishlistMutation,
  useRemoveFromWishlistMutation,
} = shopwireApi
