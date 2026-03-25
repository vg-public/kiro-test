import { useState } from 'react'
import { useDispatch } from 'react-redux'
import { useLoginMutation } from '../../api/shopwireApi'
import { setCredentials } from '../../store/authSlice'
import ErrorMessage from '../common/ErrorMessage'

interface LoginFormProps {
  onSuccess: () => void
  onSwitchToRegister: () => void
}

export default function LoginForm({ onSuccess, onSwitchToRegister }: LoginFormProps) {
  const dispatch = useDispatch()
  const [login, { isLoading }] = useLoginMutation()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)
    try {
      const result = await login({ email, password }).unwrap()
      dispatch(setCredentials({ token: result.access_token, user: result.user }))
      onSuccess()
    } catch {
      setError('Invalid email or password. Please try again.')
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <h2>Sign In</h2>
      {error && <ErrorMessage message={error} />}

      <div className="form-group">
        <label htmlFor="login-email">Email</label>
        <input
          id="login-email"
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
          autoComplete="email"
        />
      </div>

      <div className="form-group">
        <label htmlFor="login-password">Password</label>
        <input
          id="login-password"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          autoComplete="current-password"
        />
      </div>

      <button type="submit" className="submit-btn" disabled={isLoading}>
        {isLoading ? 'Signing in…' : 'Sign In'}
      </button>

      <div className="auth-switch">
        New to ShopWire?{' '}
        <button type="button" onClick={onSwitchToRegister}>
          Create account
        </button>
      </div>
    </form>
  )
}
