import type { CartItem as CartItemType } from '../../types/api'
import { useUpdateCartItemMutation, useRemoveCartItemMutation } from '../../api/shopwireApi'
import { useDispatch } from 'react-redux'
import { setCartCount } from '../../store/cartSlice'

interface CartItemProps {
  item: CartItemType
  onCartUpdate: (count: number) => void
}

export default function CartItem({ item, onCartUpdate }: CartItemProps) {
  const dispatch = useDispatch()
  const [updateItem, { isLoading: isUpdating }] = useUpdateCartItemMutation()
  const [removeItem, { isLoading: isRemoving }] = useRemoveCartItemMutation()

  const handleQtyChange = async (newQty: number) => {
    if (newQty < 1) return
    try {
      const cart = await updateItem({ cartItemId: item.cart_item_id, body: { quantity: newQty } }).unwrap()
      dispatch(setCartCount(cart.item_count))
      onCartUpdate(cart.item_count)
    } catch {
      // ignore
    }
  }

  const handleRemove = async () => {
    try {
      const cart = await removeItem(item.cart_item_id).unwrap()
      dispatch(setCartCount(cart.item_count))
      onCartUpdate(cart.item_count)
    } catch {
      // ignore
    }
  }

  return (
    <div className="cart-item">
      <div className="cart-item-img">
        {item.image_url ? (
          <img src={item.image_url} alt={item.title} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
        ) : (
          <span style={{ fontSize: 28, display: 'flex', alignItems: 'center', justifyContent: 'center', height: '100%' }}>📦</span>
        )}
      </div>
      <div className="cart-item-info">
        <div className="cart-item-title">{item.title}</div>
        <div className="cart-item-variant">{item.variant_title}</div>
        {item.is_prime && <div className="product-prime" style={{ fontSize: 11 }}>✔ Prime</div>}
        <div className="cart-item-price">${item.line_total.toFixed(2)}</div>
        <div className="cart-item-controls">
          <button
            className="qty-btn"
            onClick={() => handleQtyChange(item.quantity - 1)}
            disabled={isUpdating || item.quantity <= 1}
            aria-label="Decrease quantity"
          >
            −
          </button>
          <span className="qty-display">{item.quantity}</span>
          <button
            className="qty-btn"
            onClick={() => handleQtyChange(item.quantity + 1)}
            disabled={isUpdating}
            aria-label="Increase quantity"
          >
            +
          </button>
          <button
            className="remove-btn"
            onClick={handleRemove}
            disabled={isRemoving}
          >
            Remove
          </button>
        </div>
      </div>
    </div>
  )
}
