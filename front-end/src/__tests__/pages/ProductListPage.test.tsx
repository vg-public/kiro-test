import { describe, it, expect, vi } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { Provider } from 'react-redux'
import { configureStore } from '@reduxjs/toolkit'
import ProductListPage from '../../pages/ProductListPage'
import authReducer from '../../store/authSlice'
import cartReducer from '../../store/cartSlice'
import { shopwireApi } from '../../api/shopwireApi'

const mockProducts = [
  {
    product_id: 'p1', title: 'Laptop Pro', slug: 'laptop-pro',
    brand: 'TechPro', category: 'Electronics', primary_image: null,
    base_price: 899, sale_price: null, currency: 'USD',
    discount_pct: null, badge: 'Best Seller', avg_rating: 4,
    review_count: 500, is_prime: true, in_stock: true,
  },
  {
    product_id: 'p2', title: 'Running Shoes', slug: 'running-shoes',
    brand: 'StrideFit', category: 'Clothing', primary_image: null,
    base_price: 54.95, sale_price: null, currency: 'USD',
    discount_pct: null, badge: null, avg_rating: 4,
    review_count: 200, is_prime: true, in_stock: true,
  },
]

vi.mock('../../api/shopwireApi', async () => {
  const actual = await vi.importActual('../../api/shopwireApi')
  return {
    ...actual,
    useGetProductsQuery: () => ({
      data: {
        results: mockProducts,
        pagination: { page: 1, limit: 20, total: 2, pages: 1 },
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

function renderPage() {
  return render(
    <Provider store={makeStore()}>
      <MemoryRouter>
        <ProductListPage />
      </MemoryRouter>
    </Provider>
  )
}

describe('ProductListPage', () => {
  it('renders sidebar filters', () => {
    renderPage()
    expect(screen.getByText('Filters')).toBeInTheDocument()
  })

  it('renders results count', () => {
    renderPage()
    expect(screen.getByText(/Showing 2 of 2 results/)).toBeInTheDocument()
  })

  it('renders sort dropdown', () => {
    renderPage()
    expect(screen.getByLabelText('Sort by:')).toBeInTheDocument()
  })

  it('renders all products', () => {
    renderPage()
    expect(screen.getByText('Laptop Pro')).toBeInTheDocument()
    expect(screen.getByText('Running Shoes')).toBeInTheDocument()
  })

  it('sort dropdown has correct options', () => {
    renderPage()
    const select = screen.getByLabelText('Sort by:')
    expect(select).toBeInTheDocument()
    fireEvent.change(select, { target: { value: 'price_asc' } })
    expect((select as HTMLSelectElement).value).toBe('price_asc')
  })
})
