import { useState } from 'react'
import { useDispatch } from 'react-redux'
import { useRegisterMutation } from '../../api/shopwireApi'
import { setCredentials } from '../../store/authSlice'
import ErrorMessage from '../common/ErrorMessage'

interface RegisterFormProps {
  onSuccess: () => void
  onSwitchToLogin: () => void
}

export default function RegisterForm({ onSuccess, onSwitchToLogin }: RegisterFormProps) {
  const dispatch = useDispatch()
  const [register, { isLoading }] = useRegisterMutation()
  const [form, setForm] = useState({
    first_name: '',
    last_name: '',
    email: '',
    password: '',
    confirmPassword: '',
  })
  const [error, setError] = useState<string | null>(null)

  const set = (field: string) => (e: React.ChangeEvent<HTMLInputElement>) =>
    setForm((prev) => ({ ...prev, [field]: e.target.value }))

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)
    if (form.password !== form.confirmPassword) {
      setError('Passwords do not match.')
      return
    }
    if (form.password.length < 8) {
      setError('Password must be at least 8 characters.')
      return
    }
    try {
      const result = await register({
        email: form.email,
        password: form.password,
        first_name: form.first_name,
        last_name: form.last_name,
      }).unwrap()
      dispatch(setCredentials({ token: result.access_token, user: result.user }))
      onSuccess()
    } catch {
      setError('Registration failed. Email may already be in use.')
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <h2>Create Account</h2>
      {error && <ErrorMessage message={error} />}

      <div className="form-group">
        <label htmlFor="reg-first">First Name</label>
        <input id="reg-first" type="text" value={form.first_name} onChange={set('first_name')} required />
      </div>
      <div className="form-group">
        <label htmlFor="reg-last">Last Name</label>
        <input id="reg-last" type="text" value={form.last_name} onChange={set('last_name')} required />
      </div>
      <div className="form-group">
        <label htmlFor="reg-email">Email</label>
        <input id="reg-email" type="email" value={form.email} onChange={set('email')} required autoComplete="email" />
      </div>
      <div className="form-group">
        <label htmlFor="reg-password">Password</label>
        <input id="reg-password" type="password" value={form.password} onChange={set('password')} required autoComplete="new-password" />
      </div>
      <div className="form-group">
        <label htmlFor="reg-confirm">Confirm Password</label>
        <input id="reg-confirm" type="password" value={form.confirmPassword} onChange={set('confirmPassword')} required autoComplete="new-password" />
      </div>

      <button type="submit" className="submit-btn" disabled={isLoading}>
        {isLoading ? 'Creating account…' : 'Create Account'}
      </button>

      <div className="auth-switch">
        Already have an account?{' '}
        <button type="button" onClick={onSwitchToLogin}>
          Sign in
        </button>
      </div>
    </form>
  )
}
