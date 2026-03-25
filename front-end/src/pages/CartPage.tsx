import { useNavigate } from 'react-router-dom'
import { useDispatch } from 'react-redux'
import { useGetCartQuery } from '../api/shopwireApi'
import { setCartCount } from '../store/cartSlice'
import CartItemComponent from '../components/cart/CartItem'
import ErrorMessage from '../components/common/ErrorMessage'

export default function CartPage() {
  const navigate = useNavigate()
  const dispatch = useDispatch()
  const { data: cart, isLoading, isError } = useGetCartQuery()

  const handleCartUpdate = (count: number) => {
    dispatch(setCartCount(count))
  }

  if (isLoading) {
    return (
      <div className="loading" style={{ marginTop: 48 }}>
        <div className="spinner" />
        <p>Loading cart…</p>
      </div>
    )
  }

  if (isError) {
    return (
      <div className="cart-page">
        <ErrorMessage message="Failed to load cart." />
      </div>
    )
  }

  if (!cart || cart.items.length === 0) {
    return (
      <div className="cart-page">
        <div className="cart-empty">
          <h2>Your cart is empty</h2>
          <p>Looks like you haven't added anything yet.</p>
          <button
            className="hero-btn"
            style={{ marginTop: 16 }}
            onClick={() => navigate('/products')}
          >
            Continue Shopping
          </button>
        </div>
      </div>
    )
  }

  return (
    <div className="cart-page">
      <h1>Shopping Cart</h1>
      <div className="cart-layout">
        <div className="cart-items-section">
          {cart.items.map((item) => (
            <CartItemComponent
              key={item.cart_item_id}
              item={item}
              onCartUpdate={handleCartUpdate}
            />
          ))}
        </div>

        <div className="cart-summary-section">
          <h3>
            Subtotal ({cart.item_count} item{cart.item_count !== 1 ? 's' : ''}):
            <strong> ${cart.subtotal.toFixed(2)}</strong>
          </h3>
          <button
            className="checkout-btn"
            style={{ marginTop: 16 }}
            onClick={() => navigate('/checkout')}
          >
            Proceed to Checkout
          </button>
        </div>
      </div>
    </div>
  )
}
