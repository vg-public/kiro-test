import { useNavigate } from 'react-router-dom'
import { useGetProductsQuery } from '../api/shopwireApi'
import ProductGrid from '../components/product/ProductGrid'
import ErrorMessage from '../components/common/ErrorMessage'

export default function HomePage() {
  const navigate = useNavigate()
  const { data, isLoading, isError } = useGetProductsQuery({ limit: 8, sort: 'featured' })

  return (
    <div>
      {/* Hero Banner */}
      <div className="hero-banner">
        <div className="hero-text">
          <h1>Welcome to ShopWire</h1>
          <p>Millions of products. Great prices. Fast delivery.</p>
          <button className="hero-btn" onClick={() => navigate('/products')}>
            Shop Now
          </button>
        </div>
      </div>

      {/* Featured Products */}
      <div style={{ maxWidth: 1400, margin: '24px auto', padding: '0 12px' }}>
        <h2 style={{ fontSize: 20, marginBottom: 16 }}>Featured Products</h2>

        {isError && <ErrorMessage message="Failed to load products." />}

        {isLoading && (
          <div className="loading">
            <div className="spinner" />
            <p>Loading products…</p>
          </div>
        )}

        {data && <ProductGrid products={data.results} />}
      </div>
    </div>
  )
}
