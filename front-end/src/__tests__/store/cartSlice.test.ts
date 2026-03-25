import { describe, it, expect } from 'vitest'
import cartReducer, {
  setCartCount,
  incrementCartCount,
  openCart,
  closeCart,
  toggleCart,
} from '../../store/cartSlice'

describe('cartSlice', () => {
  it('returns initial state', () => {
    const state = cartReducer(undefined, { type: '@@INIT' })
    expect(state.itemCount).toBe(0)
    expect(state.isOpen).toBe(false)
  })

  it('setCartCount sets the count', () => {
    const state = cartReducer(undefined, setCartCount(5))
    expect(state.itemCount).toBe(5)
  })

  it('incrementCartCount increments by 1', () => {
    let state = cartReducer(undefined, setCartCount(3))
    state = cartReducer(state, incrementCartCount())
    expect(state.itemCount).toBe(4)
  })

  it('openCart sets isOpen to true', () => {
    const state = cartReducer(undefined, openCart())
    expect(state.isOpen).toBe(true)
  })

  it('closeCart sets isOpen to false', () => {
    let state = cartReducer(undefined, openCart())
    state = cartReducer(state, closeCart())
    expect(state.isOpen).toBe(false)
  })

  it('toggleCart flips isOpen', () => {
    let state = cartReducer(undefined, toggleCart())
    expect(state.isOpen).toBe(true)
    state = cartReducer(state, toggleCart())
    expect(state.isOpen).toBe(false)
  })
})
