import { useNavigate } from 'react-router-dom'
import { useDispatch } from 'react-redux'
import type { ProductSummary } from '../../types/api'
import StarRating from '../common/StarRating'
import { useAddToCartMutation } from '../../api/shopwireApi'
import { incrementCartCount } from '../../store/cartSlice'

interface ProductCardProps {
  product: ProductSummary
}

export default function ProductCard({ product }: ProductCardProps) {
  const navigate = useNavigate()
  const dispatch = useDispatch()
  const [addToCart, { isLoading }] = useAddToCartMutation()

  const displayPrice = product.sale_price ?? product.base_price
  const discount = product.discount_pct
    ? product.discount_pct
    : product.sale_price
    ? Math.round((1 - product.sale_price / product.base_price) * 100)
    : null

  const handleAddToCart = async (e: React.MouseEvent) => {
    e.stopPropagation()
    try {
      // Use first variant if available; fall back to product_id as variant placeholder
      await addToCart({ variant_id: product.product_id, quantity: 1 }).unwrap()
      dispatch(incrementCartCount())
    } catch {
      // silently fail — cart page will show error
    }
  }

  return (
    <div
      className="product-card"
      onClick={() => navigate(`/products/${product.product_id}`)}
      role="article"
      aria-label={product.title}
    >
      <div className="product-img">
        {product.primary_image ? (
          <img src={product.primary_image} alt={product.title} />
        ) : (
          '📦'
        )}
      </div>

      {product.badge && (
        <span className="product-badge">{product.badge}</span>
      )}

      <div className="product-title">{product.title}</div>

      <div className="product-rating">
        <StarRating rating={product.avg_rating} />
      </div>
      <div className="product-review-count">
        {product.review_count.toLocaleString()} reviews
      </div>

      <div className="product-price">
        <span className="currency">$</span>
        {displayPrice.toFixed(2)}
      </div>

      {product.sale_price && (
        <>
          <div className="product-original-price">
            List: ${product.base_price.toFixed(2)}
          </div>
          {discount && (
            <div className="product-discount">Save {discount}%</div>
          )}
        </>
      )}

      {product.is_prime && (
        <div className="product-prime">✔ Prime — FREE Delivery</div>
      )}

      <button
        className="add-to-cart-btn"
        onClick={handleAddToCart}
        disabled={isLoading || !product.in_stock}
        aria-label={`Add ${product.title} to cart`}
      >
        {isLoading ? 'Adding…' : product.in_stock ? 'Add to Cart' : 'Out of Stock'}
      </button>
    </div>
  )
}
