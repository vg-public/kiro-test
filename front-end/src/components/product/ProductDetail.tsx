import { useState } from 'react'
import { useDispatch } from 'react-redux'
import type { ProductDetail as ProductDetailType } from '../../types/api'
import StarRating from '../common/StarRating'
import { useAddToCartMutation } from '../../api/shopwireApi'
import { incrementCartCount } from '../../store/cartSlice'
import ErrorMessage from '../common/ErrorMessage'

interface ProductDetailProps {
  product: ProductDetailType
}

export default function ProductDetail({ product }: ProductDetailProps) {
  const dispatch = useDispatch()
  const [addToCart, { isLoading }] = useAddToCartMutation()
  const [selectedVariantId, setSelectedVariantId] = useState<string>(
    product.variants[0]?.variant_id ?? ''
  )
  const [activeImage, setActiveImage] = useState(0)
  const [addError, setAddError] = useState<string | null>(null)
  const [addedMsg, setAddedMsg] = useState(false)

  const selectedVariant = product.variants.find(
    (v) => v.variant_id === selectedVariantId
  )
  const displayPrice = selectedVariant?.price ?? product.sale_price ?? product.base_price

  const handleAddToCart = async () => {
    setAddError(null)
    try {
      const variantId = selectedVariantId || product.product_id
      await addToCart({ variant_id: variantId, quantity: 1 }).unwrap()
      dispatch(incrementCartCount())
      setAddedMsg(true)
      setTimeout(() => setAddedMsg(false), 2000)
    } catch {
      setAddError('Failed to add to cart. Please try again.')
    }
  }

  const images = product.images.length > 0 ? product.images : null

  return (
    <div className="product-detail-layout">
      {/* Images */}
      <div className="product-images">
        <div className="product-main-img">
          {images ? (
            <img src={images[activeImage]?.url} alt={images[activeImage]?.alt_text} />
          ) : (
            '📦'
          )}
        </div>
        {images && images.length > 1 && (
          <div className="product-thumbnails">
            {images.map((img, i) => (
              <div
                key={i}
                className={`product-thumbnail${i === activeImage ? ' active' : ''}`}
                onClick={() => setActiveImage(i)}
              >
                <img src={img.url} alt={img.alt_text} />
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Info */}
      <div className="product-info">
        {product.brand && <div className="product-brand">by {product.brand}</div>}
        <h1>{product.title}</h1>

        <div style={{ display: 'flex', alignItems: 'center', gap: 8, margin: '8px 0' }}>
          <StarRating rating={product.avg_rating} size={16} />
          <span style={{ fontSize: 13, color: '#007185' }}>
            {product.review_count.toLocaleString()} reviews
          </span>
        </div>

        {product.badge && <span className="product-badge">{product.badge}</span>}

        <div className="product-detail-price">
          <span className="currency" style={{ fontSize: 14, verticalAlign: 'super' }}>$</span>
          {displayPrice.toFixed(2)}
          {product.sale_price && (
            <>
              <span className="original">${product.base_price.toFixed(2)}</span>
              {product.discount_pct && (
                <span className="discount">Save {product.discount_pct}%</span>
              )}
            </>
          )}
        </div>

        <div className={product.in_stock ? 'in-stock' : 'out-of-stock'}>
          {product.in_stock ? 'In Stock' : 'Out of Stock'}
        </div>

        {product.is_prime && (
          <div className="product-prime" style={{ marginTop: 8 }}>✔ Prime — FREE Delivery</div>
        )}

        {product.bullet_points.length > 0 && (
          <ul className="product-bullets">
            {product.bullet_points.map((bp, i) => (
              <li key={i}>{bp}</li>
            ))}
          </ul>
        )}

        {/* Variants */}
        {product.variants.length > 0 && (
          <div className="variant-section">
            <h4>Options</h4>
            <div className="variant-options">
              {product.variants.map((v) => (
                <button
                  key={v.variant_id}
                  className={`variant-btn${v.variant_id === selectedVariantId ? ' active' : ''}`}
                  onClick={() => setSelectedVariantId(v.variant_id)}
                  disabled={!v.is_active || v.stock_qty === 0}
                >
                  {v.title}
                </button>
              ))}
            </div>
          </div>
        )}

        {addError && <ErrorMessage message={addError} />}

        <button
          className="add-to-cart-detail-btn"
          onClick={handleAddToCart}
          disabled={isLoading || !product.in_stock}
        >
          {addedMsg ? '✓ Added to Cart' : isLoading ? 'Adding…' : 'Add to Cart'}
        </button>

        {product.description && (
          <div style={{ marginTop: 20 }}>
            <h3 style={{ fontSize: 15, marginBottom: 8 }}>About this item</h3>
            <p style={{ fontSize: 13, lineHeight: 1.6, color: '#333' }}>
              {product.description}
            </p>
          </div>
        )}
      </div>
    </div>
  )
}
