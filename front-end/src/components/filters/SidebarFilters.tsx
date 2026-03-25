interface FilterState {
  departments: string[]
  priceRange: string
  rating: string
  inStock: boolean
  prime: boolean
}

interface SidebarFiltersProps {
  filters: FilterState
  onChange: (filters: FilterState) => void
}

const DEPARTMENTS = ['Electronics', 'Clothing', 'Books', 'Home & Garden', 'Sports', 'Toys', 'Automotive']

const PRICE_RANGES = [
  { label: 'Under $25', value: 'under25' },
  { label: '$25 – $50', value: '25-50' },
  { label: '$50 – $100', value: '50-100' },
  { label: '$100 – $200', value: '100-200' },
  { label: 'Over $200', value: 'over200' },
]

const RATINGS = [
  { label: '★★★★☆ & Up', value: '4' },
  { label: '★★★☆☆ & Up', value: '3' },
  { label: '★★☆☆☆ & Up', value: '2' },
]

export default function SidebarFilters({ filters, onChange }: SidebarFiltersProps) {
  const toggleDept = (dept: string) => {
    const next = filters.departments.includes(dept)
      ? filters.departments.filter((d) => d !== dept)
      : [...filters.departments, dept]
    onChange({ ...filters, departments: next })
  }

  const clearAll = () => {
    onChange({ departments: [], priceRange: '', rating: '', inStock: false, prime: false })
  }

  return (
    <aside className="sidebar">
      <h3>Filters</h3>

      <div className="filter-group">
        <h4>Department</h4>
        {DEPARTMENTS.map((dept) => (
          <label key={dept}>
            <input
              type="checkbox"
              checked={filters.departments.includes(dept)}
              onChange={() => toggleDept(dept)}
            />
            {dept}
          </label>
        ))}
      </div>

      <div className="filter-group">
        <h4>Price Range</h4>
        {PRICE_RANGES.map((pr) => (
          <label key={pr.value}>
            <input
              type="radio"
              name="price"
              checked={filters.priceRange === pr.value}
              onChange={() => onChange({ ...filters, priceRange: pr.value })}
            />
            {pr.label}
          </label>
        ))}
      </div>

      <div className="filter-group">
        <h4>Avg. Customer Review</h4>
        {RATINGS.map((r) => (
          <label key={r.value}>
            <input
              type="radio"
              name="rating"
              checked={filters.rating === r.value}
              onChange={() => onChange({ ...filters, rating: r.value })}
            />
            {r.label}
          </label>
        ))}
      </div>

      <div className="filter-group">
        <h4>Availability</h4>
        <label>
          <input
            type="checkbox"
            checked={filters.inStock}
            onChange={(e) => onChange({ ...filters, inStock: e.target.checked })}
          />
          In Stock
        </label>
        <label>
          <input
            type="checkbox"
            checked={filters.prime}
            onChange={(e) => onChange({ ...filters, prime: e.target.checked })}
          />
          Prime Eligible
        </label>
      </div>

      <button className="clear-filters-btn" onClick={clearAll}>
        Clear Filters
      </button>
    </aside>
  )
}

export type { FilterState }
