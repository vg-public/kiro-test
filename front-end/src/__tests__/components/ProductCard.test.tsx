import { describe, it, expect, vi } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { Provider } from 'react-redux'
import { configureStore } from '@reduxjs/toolkit'
import ProductCard from '../../components/product/ProductCard'
import authReducer from '../../store/authSlice'
import cartReducer from '../../store/cartSlice'
import { shopwireApi } from '../../api/shopwireApi'
import type { ProductSummary } from '../../types/api'

const mockNavigate = vi.fn()
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom')
  return { ...actual, useNavigate: () => mockNavigate }
})

const mockProduct: ProductSummary = {
  product_id: 'prod-001',
  title: 'Test Laptop',
  slug: 'test-laptop',
  brand: 'TechPro',
  category: 'Electronics',
  primary_image: null,
  base_price: 999.99,
  sale_price: 799.99,
  currency: 'USD',
  discount_pct: 20,
  badge: 'Best Seller',
  avg_rating: 4.5,
  review_count: 1234,
  is_prime: true,
  in_stock: true,
}

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

function renderCard(product = mockProduct) {
  return render(
    <Provider store={makeStore()}>
      <MemoryRouter>
        <ProductCard product={product} />
      </MemoryRouter>
    </Provider>
  )
}

describe('ProductCard', () => {
  it('renders product title', () => {
    renderCard()
    expect(screen.getByText('Test Laptop')).toBeInTheDocument()
  })

  it('renders sale price', () => {
    renderCard()
    expect(screen.getByText('799.99')).toBeInTheDocument()
  })

  it('renders badge', () => {
    renderCard()
    expect(screen.getByText('Best Seller')).toBeInTheDocument()
  })

  it('renders Prime indicator', () => {
    renderCard()
    expect(screen.getByText(/Prime/)).toBeInTheDocument()
  })

  it('renders review count', () => {
    renderCard()
    expect(screen.getByText(/1,234 reviews/)).toBeInTheDocument()
  })

  it('shows Add to Cart when in stock', () => {
    renderCard()
    expect(screen.getByRole('button', { name: /Add Test Laptop to cart/i })).toBeInTheDocument()
  })

  it('shows Out of Stock when not in stock', () => {
    renderCard({ ...mockProduct, in_stock: false })
    expect(screen.getByText('Out of Stock')).toBeInTheDocument()
  })

  it('navigates to product detail on card click', () => {
    renderCard()
    fireEvent.click(screen.getByRole('article'))
    expect(mockNavigate).toHaveBeenCalledWith('/products/prod-001')
  })

  it('does not navigate when Add to Cart is clicked', () => {
    mockNavigate.mockClear()
    renderCard()
    fireEvent.click(screen.getByRole('button', { name: /Add Test Laptop to cart/i }))
    expect(mockNavigate).not.toHaveBeenCalled()
  })

  it('renders placeholder emoji when no image', () => {
    renderCard()
    expect(screen.getByText('📦')).toBeInTheDocument()
  })
})
