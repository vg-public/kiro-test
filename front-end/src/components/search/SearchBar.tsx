import { useState } from 'react'
import { useNavigate } from 'react-router-dom'

const CATEGORIES = [
  'All Categories',
  'Electronics',
  'Clothing',
  'Books',
  'Home & Garden',
  'Sports',
  'Toys',
  'Automotive',
]

export default function SearchBar() {
  const [query, setQuery] = useState('')
  const [category, setCategory] = useState('All Categories')
  const navigate = useNavigate()

  const handleSearch = () => {
    if (!query.trim()) return
    const params = new URLSearchParams({ q: query.trim() })
    if (category !== 'All Categories') {
      params.set('category', category.toLowerCase().replace(/\s+/g, '-'))
    }
    navigate(`/search?${params.toString()}`)
  }

  return (
    <div className="header-search">
      <select
        className="search-category"
        value={category}
        onChange={(e) => setCategory(e.target.value)}
        aria-label="Search category"
      >
        {CATEGORIES.map((c) => (
          <option key={c}>{c}</option>
        ))}
      </select>
      <input
        type="text"
        className="search-input"
        placeholder="Search products..."
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
        aria-label="Search products"
      />
      <button className="search-btn" onClick={handleSearch} aria-label="Search">
        🔍
      </button>
    </div>
  )
}
