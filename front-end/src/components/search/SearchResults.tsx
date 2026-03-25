import type { ProductSummary } from '../../types/api'
import ProductGrid from '../product/ProductGrid'
import ErrorMessage from '../common/ErrorMessage'

interface SearchResultsProps {
  results: ProductSummary[]
  query: string
  total: number
  isLoading: boolean
  error?: string
}

export default function SearchResults({
  results,
  query,
  total,
  isLoading,
  error,
}: SearchResultsProps) {
  if (error) return <ErrorMessage message={error} />

  if (isLoading) {
    return (
      <div className="loading">
        <div className="spinner" />
        <p>Searching...</p>
      </div>
    )
  }

  return (
    <div>
      <div className="results-bar">
        <span className="results-count">
          {total} result{total !== 1 ? 's' : ''} for &ldquo;{query}&rdquo;
        </span>
      </div>
      <ProductGrid products={results} />
    </div>
  )
}
