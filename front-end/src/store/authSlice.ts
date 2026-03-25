import { createSlice, type PayloadAction } from '@reduxjs/toolkit'
import type { UserProfile } from '../types/api'

interface AuthState {
  token: string | null
  user: UserProfile | null
}

const initialState: AuthState = {
  token: null,
  user: null,
}

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setCredentials(
      state,
      action: PayloadAction<{ token: string; user: UserProfile }>
    ) {
      state.token = action.payload.token
      state.user = action.payload.user
    },
    logout(state) {
      state.token = null
      state.user = null
    },
    updateUser(state, action: PayloadAction<UserProfile>) {
      state.user = action.payload
    },
  },
})

export const { setCredentials, logout, updateUser } = authSlice.actions
export default authSlice.reducer
