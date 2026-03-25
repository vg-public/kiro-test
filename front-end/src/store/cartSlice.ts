import { createSlice, type PayloadAction } from '@reduxjs/toolkit'

interface CartState {
  itemCount: number
  isOpen: boolean
}

const initialState: CartState = {
  itemCount: 0,
  isOpen: false,
}

const cartSlice = createSlice({
  name: 'cart',
  initialState,
  reducers: {
    setCartCount(state, action: PayloadAction<number>) {
      state.itemCount = action.payload
    },
    incrementCartCount(state) {
      state.itemCount += 1
    },
    openCart(state) {
      state.isOpen = true
    },
    closeCart(state) {
      state.isOpen = false
    },
    toggleCart(state) {
      state.isOpen = !state.isOpen
    },
  },
})

export const {
  setCartCount,
  incrementCartCount,
  openCart,
  closeCart,
  toggleCart,
} = cartSlice.actions
export default cartSlice.reducer
