import { describe, it, expect } from 'vitest'
import authReducer, { setCredentials, logout, updateUser } from '../../store/authSlice'
import type { UserProfile } from '../../types/api'

const mockUser: UserProfile = {
  user_id: 'user-123',
  email: 'test@example.com',
  first_name: 'Jane',
  last_name: 'Doe',
  phone: null,
  is_verified: true,
  created_at: '2024-01-01T00:00:00Z',
}

describe('authSlice', () => {
  it('returns initial state', () => {
    const state = authReducer(undefined, { type: '@@INIT' })
    expect(state.token).toBeNull()
    expect(state.user).toBeNull()
  })

  it('setCredentials stores token and user', () => {
    const state = authReducer(
      undefined,
      setCredentials({ token: 'abc123', user: mockUser })
    )
    expect(state.token).toBe('abc123')
    expect(state.user).toEqual(mockUser)
  })

  it('logout clears token and user', () => {
    const loggedIn = authReducer(
      undefined,
      setCredentials({ token: 'abc123', user: mockUser })
    )
    const state = authReducer(loggedIn, logout())
    expect(state.token).toBeNull()
    expect(state.user).toBeNull()
  })

  it('updateUser replaces user data', () => {
    const loggedIn = authReducer(
      undefined,
      setCredentials({ token: 'abc123', user: mockUser })
    )
    const updated = { ...mockUser, first_name: 'Updated' }
    const state = authReducer(loggedIn, updateUser(updated))
    expect(state.user?.first_name).toBe('Updated')
    expect(state.token).toBe('abc123')
  })
})
