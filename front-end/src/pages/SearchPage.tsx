import { useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { useSearchProductsQuery } from '../api/shopwireApi'
import SearchResults from '../components/search/SearchResults'
import SidebarFilters, { type FilterState } from '../components/filters/SidebarFilters'
import Pagination from '../components/common/Pagination'

const PRICE_MAP: Record<string, { price_min?: number; price_max?: number }> = {
  under25: { price_max: 25 },
  '25-50': { price_min: 25, price_max: 50 },
  '50-100': { price_min: 50, price_max: 100 },
  '100-200': { price_min: 100, price_max: 200 },
  over200: { price_min: 200 },
}

export default function SearchPage() {
  const [searchParams] = useSearchParams()
  const query = searchParams.get('q') ?? ''
  const [page, setPage] = useState(1)
  const [filters, setFilters] = useState<FilterState>({
    departments: [],
    priceRange: '',
    rating: '',
    inStock: false,
    prime: false,
  })

  const { data, isLoading, isError } = useSearchProductsQuery(
    {
      q: query,
      page,
      limit: 20,
      ...(filters.priceRange ? PRICE_MAP[filters.priceRange] : {}),
    },
    { skip: !query }
  )

  const handleFilterChange = (newFilters: FilterState) => {
    setFilters(newFilters)
    setPage(1)
  }

  return (
    <div className="main-layout">
      <SidebarFilters filters={filters} onChange={handleFilterChange} />
      <main className="product-area">
        <SearchResults
          results={data?.results ?? []}
          query={query}
          total={data?.pagination.total ?? 0}
          isLoading={isLoading}
          error={isError ? 'Search failed. Please try again.' : undefined}
        />
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
