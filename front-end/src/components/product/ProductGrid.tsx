import type { ProductSummary } from '../../types/api'
import ProductCard from './ProductCard'

interface ProductGridProps {
  products: ProductSummary[]
}

export default function ProductGrid({ products }: ProductGridProps) {
  if (products.length === 0) {
    return (
      <div className="product-grid">
        <div className="no-results">😕 No products found. Try a different search.</div>
      </div>
    )
  }

  return (
    <div className="product-grid">
      {products.map((product) => (
        <ProductCard key={product.product_id} product={product} />
      ))}
    </div>
  )
}
