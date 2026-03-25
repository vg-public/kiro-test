import { useNavigate } from 'react-router-dom'
import { useDispatch, useSelector } from 'react-redux'
import type { RootState } from '../../store'
import { logout } from '../../store/authSlice'
import { toggleCart } from '../../store/cartSlice'
import SearchBar from '../search/SearchBar'

export default function Header() {
  const navigate = useNavigate()
  const dispatch = useDispatch()
  const { user } = useSelector((s: RootState) => s.auth)
  const { itemCount } = useSelector((s: RootState) => s.cart)

  const handleLogout = () => {
    dispatch(logout())
    navigate('/')
  }

  return (
    <header className="header">
      <div className="header-logo" onClick={() => navigate('/')}>
        ShopWire
      </div>

      <SearchBar />

      <div className="header-actions">
        {user ? (
          <>
            <span className="header-link" style={{ cursor: 'default' }}>
              Hello, {user.first_name}
            </span>
            <button className="header-link" onClick={() => navigate('/orders')}>
              Orders
            </button>
            <button className="header-link" onClick={handleLogout}>
              Sign Out
            </button>
          </>
        ) : (
          <button className="header-link" onClick={() => navigate('/auth')}>
            Sign In
          </button>
        )}
        <button className="header-link" onClick={() => navigate('/orders')}>
          Returns
        </button>
        <button
          className="header-link cart-link"
          onClick={() => dispatch(toggleCart())}
          aria-label={`Cart with ${itemCount} items`}
        >
          🛒 Cart <span className="cart-count">{itemCount}</span>
        </button>
      </div>
    </header>
  )
}
