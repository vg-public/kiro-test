import { describe, it, expect, vi } from 'vitest'
import { render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { Provider } from 'react-redux'
import { configureStore } from '@reduxjs/toolkit'
import HomePage from '../../pages/HomePage'
import authReducer from '../../store/authSlice'
import cartReducer from '../../store/cartSlice'
import { shopwireApi } from '../../api/shopwireApi'

vi.mock('../../api/shopwireApi', async () => {
  const actual = await vi.importActual('../../api/shopwireApi')
  return {
    ...actual,
    useGetProductsQuery: () => ({
      data: {
        results: [
          {
            product_id: 'p1',
            title: 'Featured Laptop',
            slug: 'featured-laptop',
            brand: 'TechPro',
            category: 'Electronics',
            primary_image: null,
            base_price: 999,
            sale_price: null,
            currency: 'USD',
            discount_pct: null,
            badge: null,
            avg_rating: 4,
            review_count: 100,
            is_prime: true,
            in_stock: true,
          },
        ],
        pagination: { page: 1, limit: 8, total: 1, pages: 1 },
        query: '',
        facets: { categories: [], brands: [], price_range: { min: 0, max: 1000 } },
      },
      isLoading: false,
      isError: false,
    }),
  }
})

function makeStore() {
  return configureStore({
    reducer: {
      auth: authReducer,
      cart: cartReducer,
      [shopwireApi.reducerPath]: shopwireApi.reducer,
    },
    middleware: (gDM) => gDM().concat(shopwireApi.middleware),
  })
}

describe('HomePage', () => {
  it('renders hero banner', () => {
    render(
      <Provider store={makeStore()}>
        <MemoryRouter>
          <HomePage />
        </MemoryRouter>
      </Provider>
    )
    expect(screen.getByText('Welcome to ShopWire')).toBeInTheDocument()
  })

  it('renders Shop Now button', () => {
    render(
      <Provider store={makeStore()}>
        <MemoryRouter>
          <HomePage />
        </MemoryRouter>
      </Provider>
    )
    expect(screen.getByText('Shop Now')).toBeInTheDocument()
  })

  it('renders featured products section', () => {
    render(
      <Provider store={makeStore()}>
        <MemoryRouter>
          <HomePage />
        </MemoryRouter>
      </Provider>
    )
    expect(screen.getByText('Featured Products')).toBeInTheDocument()
  })

  it('renders product from API data', () => {
    render(
      <Provider store={makeStore()}>
        <MemoryRouter>
          <HomePage />
        </MemoryRouter>
      </Provider>
    )
    expect(screen.getByText('Featured Laptop')).toBeInTheDocument()
  })
})
