import { useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { useGetProductsQuery } from '../api/shopwireApi'
import SidebarFilters, { type FilterState } from '../components/filters/SidebarFilters'
import ProductGrid from '../components/product/ProductGrid'
import Pagination from '../components/common/Pagination'
import ErrorMessage from '../components/common/ErrorMessage'
import type { ProductListParams } from '../types/api'

const SORT_OPTIONS = [
  { label: 'Featured', value: 'featured' },
  { label: 'Price: Low to High', value: 'price_asc' },
  { label: 'Price: High to Low', value: 'price_desc' },
  { label: 'Avg. Customer Review', value: 'rating' },
  { label: 'Newest Arrivals', value: 'newest' },
] as const

const PRICE_MAP: Record<string, { price_min?: number; price_max?: number }> = {
  under25: { price_max: 25 },
  '25-50': { price_min: 25, price_max: 50 },
  '50-100': { price_min: 50, price_max: 100 },
  '100-200': { price_min: 100, price_max: 200 },
  over200: { price_min: 200 },
}

export default function ProductListPage() {
  const [searchParams] = useSearchParams()
  const [page, setPage] = useState(1)
  const [sort, setSort] = useState<ProductListParams['sort']>('featured')
  const [filters, setFilters] = useState<FilterState>({
    departments: [],
    priceRange: '',
    rating: '',
    inStock: false,
    prime: false,
  })

  const categoryParam = searchParams.get('category') ?? undefined

  const queryParams: ProductListParams = {
    page,
    limit: 20,
    sort,
    category: filters.departments.length === 1
      ? filters.departments[0].toLowerCase().replace(/\s+/g, '-')
      : categoryParam,
    ...(filters.priceRange ? PRICE_MAP[filters.priceRange] : {}),
    ...(filters.rating ? { rating: parseInt(filters.rating) } : {}),
    ...(filters.inStock ? { in_stock: true } : {}),
    ...(filters.prime ? { prime: true } : {}),
  }

  const { data, isLoading, isError } = useGetProductsQuery(queryParams)

  const handleFilterChange = (newFilters: FilterState) => {
    setFilters(newFilters)
    setPage(1)
  }

  return (
    <div className="main-layout">
      <SidebarFilters filters={filters} onChange={handleFilterChange} />

      <main className="product-area">
        <div className="results-bar">
          <span className="results-count">
            {data ? `Showing ${data.results.length} of ${data.pagination.total} results` : 'Loading…'}
          </span>
          <div className="sort-controls">
            <label htmlFor="sort-select">Sort by:</label>
            <select
              id="sort-select"
              className="sort-select"
              value={sort}
              onChange={(e) => {
                setSort(e.target.value as ProductListParams['sort'])
                setPage(1)
              }}
            >
              {SORT_OPTIONS.map((o) => (
                <option key={o.value} value={o.value}>{o.label}</option>
              ))}
            </select>
          </div>
        </div>

        {isError && <ErrorMessage message="Failed to load products." />}

        {isLoading && (
          <div className="loading">
            <div className="spinner" />
            <p>Loading products…</p>
          </div>
        )}

        {data && <ProductGrid products={data.results} />}

        {data && (
          <Pagination
            page={data.pagination.page}
            pages={data.pagination.pages}
            onPageChange={setPage}
          />
        )}
      </main>
    </div>
  )
}
