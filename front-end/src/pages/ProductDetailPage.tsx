import { useParams, useNavigate } from 'react-router-dom'
import { useGetProductQuery, useGetProductReviewsQuery } from '../api/shopwireApi'
import ProductDetailComponent from '../components/product/ProductDetail'
import StarRating from '../components/common/StarRating'
import ErrorMessage from '../components/common/ErrorMessage'

export default function ProductDetailPage() {
  const { productId } = useParams<{ productId: string }>()
  const navigate = useNavigate()

  const { data: product, isLoading, isError } = useGetProductQuery(productId ?? '')
  const { data: reviewsData } = useGetProductReviewsQuery(
    { productId: productId ?? '' },
    { skip: !productId }
  )

  if (isLoading) {
    return (
      <div className="loading" style={{ marginTop: 48 }}>
        <div className="spinner" />
        <p>Loading product…</p>
      </div>
    )
  }

  if (isError || !product) {
    return (
      <div style={{ maxWidth: 600, margin: '48px auto', padding: '0 16px' }}>
        <ErrorMessage message="Product not found." />
        <button
          onClick={() => navigate('/products')}
          style={{ marginTop: 16, padding: '8px 16px', cursor: 'pointer' }}
        >
          ← Back to Products
        </button>
      </div>
    )
  }

  return (
    <div className="product-detail-page">
      <button
        onClick={() => navigate(-1)}
        style={{ background: 'none', border: 'none', color: '#007185', cursor: 'pointer', fontSize: 13, marginBottom: 12 }}
      >
        ← Back
      </button>

      <ProductDetailComponent product={product} />

      {/* Reviews */}
      {reviewsData && reviewsData.reviews.length > 0 && (
        <div className="reviews-section">
          <h2>Customer Reviews</h2>
          {reviewsData.reviews.map((review) => (
            <div key={review.review_id} className="review-card">
              <div className="review-header">
                <StarRating rating={review.rating} size={13} />
                <span className="review-author">{review.user_name}</span>
                {review.is_verified && (
                  <span className="review-verified">Verified Purchase</span>
                )}
              </div>
              {review.title && <div className="review-title">{review.title}</div>}
              {review.body && <div className="review-body">{review.body}</div>}
              <div className="review-date">
                {new Date(review.created_at).toLocaleDateString()}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
