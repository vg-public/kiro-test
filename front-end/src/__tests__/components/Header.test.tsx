import { describe, it, expect, vi } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { Provider } from 'react-redux'
import { configureStore } from '@reduxjs/toolkit'
import Header from '../../components/layout/Header'
import authReducer from '../../store/authSlice'
import cartReducer from '../../store/cartSlice'
import { shopwireApi } from '../../api/shopwireApi'

const mockNavigate = vi.fn()
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom')
  return { ...actual, useNavigate: () => mockNavigate }
})

function makeStore(preloadedState = {}) {
  return configureStore({
    reducer: {
      auth: authReducer,
      cart: cartReducer,
      [shopwireApi.reducerPath]: shopwireApi.reducer,
    },
    middleware: (gDM) => gDM().concat(shopwireApi.middleware),
    preloadedState,
  })
}

function renderHeader(preloadedState = {}) {
  const store = makeStore(preloadedState)
  return render(
    <Provider store={store}>
      <MemoryRouter>
        <Header />
      </MemoryRouter>
    </Provider>
  )
}

describe('Header', () => {
  it('renders the ShopWire logo', () => {
    renderHeader()
    expect(screen.getByText('ShopWire')).toBeInTheDocument()
  })

  it('shows Sign In when not authenticated', () => {
    renderHeader()
    expect(screen.getByText('Sign In')).toBeInTheDocument()
  })

  it('shows user first name when authenticated', () => {
    renderHeader({
      auth: {
        token: 'tok',
        user: {
          user_id: 'u1', email: 'a@b.com', first_name: 'Alice',
          last_name: 'M', phone: null, is_verified: true, created_at: '',
        },
      },
    })
    expect(screen.getByText(/Hello, Alice/)).toBeInTheDocument()
  })

  it('shows cart count from store', () => {
    renderHeader({ cart: { itemCount: 3, isOpen: false } })
    expect(screen.getByText('3')).toBeInTheDocument()
  })

  it('navigates to / when logo is clicked', () => {
    renderHeader()
    fireEvent.click(screen.getByText('ShopWire'))
    expect(mockNavigate).toHaveBeenCalledWith('/')
  })

  it('navigates to /auth when Sign In is clicked', () => {
    renderHeader()
    fireEvent.click(screen.getByText('Sign In'))
    expect(mockNavigate).toHaveBeenCalledWith('/auth')
  })

  it('cart button has accessible aria-label', () => {
    renderHeader({ cart: { itemCount: 2, isOpen: false } })
    expect(screen.getByLabelText('Cart with 2 items')).toBeInTheDocument()
  })
})
