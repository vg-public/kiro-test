import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useSelector } from 'react-redux'
import type { RootState } from '../store'
import {
  useGetCartQuery,
  useGetAddressesQuery,
  usePlaceOrderMutation,
  useValidateCouponMutation,
} from '../api/shopwireApi'
import ErrorMessage from '../components/common/ErrorMessage'

export default function CheckoutPage() {
  const navigate = useNavigate()
  const { user } = useSelector((s: RootState) => s.auth)
  const { data: cart } = useGetCartQuery()
  const { data: addresses } = useGetAddressesQuery(undefined, { skip: !user })
  const [placeOrder, { isLoading: isPlacing }] = usePlaceOrderMutation()
  const [validateCoupon] = useValidateCouponMutation()

  const [selectedAddressId, setSelectedAddressId] = useState('')
  const [couponCode, setCouponCode] = useState('')
  const [couponMsg, setCouponMsg] = useState<string | null>(null)
  const [discount, setDiscount] = useState(0)
  const [error, setError] = useState<string | null>(null)

  if (!user) {
    return (
      <div className="checkout-page">
        <ErrorMessage message="Please sign in to checkout." />
        <button onClick={() => navigate('/auth')} className="submit-btn" style={{ marginTop: 16 }}>
          Sign In
        </button>
      </div>
    )
  }

  if (!cart || cart.items.length === 0) {
    return (
      <div className="checkout-page">
        <ErrorMessage message="Your cart is empty." type="warning" />
        <button onClick={() => navigate('/products')} className="submit-btn" style={{ marginTop: 16 }}>
          Continue Shopping
        </button>
      </div>
    )
  }

  const handleApplyCoupon = async () => {
    if (!couponCode.trim()) return
    try {
      const result = await validateCoupon({ code: couponCode, cart_total: cart.subtotal }).unwrap()
      if (result.valid) {
        const disc = result.discount_type === 'percentage'
          ? (cart.subtotal * result.discount_value) / 100
          : result.discount_value
        setDiscount(disc)
        setCouponMsg(`✓ ${result.message}`)
      } else {
        setCouponMsg(`✗ ${result.message}`)
        setDiscount(0)
      }
    } catch {
      setCouponMsg('Failed to validate coupon.')
    }
  }

  const shipping = 0
  const tax = cart.subtotal * 0.08
  const total = cart.subtotal - discount + shipping + tax

  const handlePlaceOrder = async () => {
    if (!selectedAddressId) {
      setError('Please select a shipping address.')
      return
    }
    setError(null)
    try {
      const order = await placeOrder({
        address_id: selectedAddressId,
        coupon_code: couponCode || undefined,
      }).unwrap()
      navigate(`/orders`)
      console.log('Order placed:', order.order_id)
    } catch {
      setError('Failed to place order. Please try again.')
    }
  }

  return (
    <div className="checkout-page">
      <h1>Checkout</h1>
      <div className="checkout-layout">
        <div>
          {/* Shipping Address */}
          <div className="checkout-section">
            <h2>Shipping Address</h2>
            {addresses && addresses.length > 0 ? (
              addresses.map((addr) => (
                <label key={addr.address_id} style={{ display: 'flex', gap: 8, marginBottom: 8, cursor: 'pointer' }}>
                  <input
                    type="radio"
                    name="address"
                    value={addr.address_id}
                    checked={selectedAddressId === addr.address_id}
                    onChange={() => setSelectedAddressId(addr.address_id)}
                  />
                  <span style={{ fontSize: 13 }}>
                    <strong>{addr.full_name}</strong> — {addr.line1}, {addr.city}, {addr.state} {addr.postal_code}
                  </span>
                </label>
              ))
            ) : (
              <p style={{ fontSize: 13, color: '#555' }}>No saved addresses. Please add one in your account.</p>
            )}
          </div>

          {/* Coupon */}
          <div className="checkout-section">
            <h2>Coupon Code</h2>
            <div style={{ display: 'flex', gap: 8 }}>
              <input
                type="text"
                value={couponCode}
                onChange={(e) => setCouponCode(e.target.value)}
                placeholder="Enter coupon code"
                style={{ flex: 1, padding: '8px 12px', border: '1px solid #ccc', borderRadius: 4, fontSize: 14 }}
              />
              <button
                onClick={handleApplyCoupon}
                style={{ padding: '8px 16px', background: '#ff9900', border: 'none', borderRadius: 4, cursor: 'pointer', fontWeight: 'bold' }}
              >
                Apply
              </button>
            </div>
            {couponMsg && (
              <p style={{ fontSize: 13, marginTop: 8, color: couponMsg.startsWith('✓') ? '#007600' : '#cc0c39' }}>
                {couponMsg}
              </p>
            )}
          </div>

          {error && <ErrorMessage message={error} />}
        </div>

        {/* Order Summary */}
        <div className="order-summary-card">
          <h2>Order Summary</h2>
          {cart.items.map((item) => (
            <div key={item.cart_item_id} className="summary-row">
              <span style={{ fontSize: 12 }}>{item.title} × {item.quantity}</span>
              <span>${item.line_total.toFixed(2)}</span>
            </div>
          ))}
          <div className="summary-row" style={{ borderTop: '1px solid #eee', paddingTop: 8, marginTop: 8 }}>
            <span>Subtotal</span>
            <span>${cart.subtotal.toFixed(2)}</span>
          </div>
          {discount > 0 && (
            <div className="summary-row" style={{ color: '#007600' }}>
              <span>Discount</span>
              <span>−${discount.toFixed(2)}</span>
            </div>
          )}
          <div className="summary-row">
            <span>Shipping</span>
            <span>{shipping === 0 ? 'FREE' : `$${shipping.toFixed(2)}`}</span>
          </div>
          <div className="summary-row">
            <span>Tax (8%)</span>
            <span>${tax.toFixed(2)}</span>
          </div>
          <div className="summary-row total">
            <span>Order Total</span>
            <span>${total.toFixed(2)}</span>
          </div>
          <button
            className="place-order-btn"
            onClick={handlePlaceOrder}
            disabled={isPlacing}
          >
            {isPlacing ? 'Placing Order…' : 'Place Order'}
          </button>
        </div>
      </div>
    </div>
  )
}
