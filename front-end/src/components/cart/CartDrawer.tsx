import { useNavigate } from 'react-router-dom'
import { useDispatch } from 'react-redux'
import { closeCart, setCartCount } from '../../store/cartSlice'
import { useGetCartQuery } from '../../api/shopwireApi'
import CartItemComponent from './CartItem'

export default function CartDrawer() {
  const dispatch = useDispatch()
  const navigate = useNavigate()
  const { data: cart, isLoading } = useGetCartQuery()

  const handleCartUpdate = (count: number) => {
    dispatch(setCartCount(count))
  }

  const handleCheckout = () => {
    dispatch(closeCart())
    navigate('/checkout')
  }

  return (
    <>
      <div className="cart-drawer-overlay" onClick={() => dispatch(closeCart())} />
      <div className="cart-drawer" role="dialog" aria-label="Shopping cart">
        <div className="cart-drawer-header">
          <h2>Shopping Cart</h2>
          <button
            className="cart-close-btn"
            onClick={() => dispatch(closeCart())}
            aria-label="Close cart"
          >
            ×
          </button>
        </div>

        <div className="cart-drawer-body">
          {isLoading && (
            <div className="loading">
              <div className="spinner" />
            </div>
          )}
          {!isLoading && (!cart || cart.items.length === 0) && (
            <div style={{ textAlign: 'center', padding: 32, color: '#555' }}>
              Your cart is empty.
            </div>
          )}
          {cart?.items.map((item) => (
            <CartItemComponent
              key={item.cart_item_id}
              item={item}
              onCartUpdate={handleCartUpdate}
            />
          ))}
        </div>

        {cart && cart.items.length > 0 && (
          <div className="cart-drawer-footer">
            <div className="cart-subtotal">
              Subtotal ({cart.item_count} item{cart.item_count !== 1 ? 's' : ''}):
              <strong> ${cart.subtotal.toFixed(2)}</strong>
            </div>
            <button className="checkout-btn" onClick={handleCheckout}>
              Proceed to Checkout
            </button>
          </div>
        )}
      </div>
    </>
  )
}
