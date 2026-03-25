import { describe, it, expect, vi } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import SidebarFilters, { type FilterState } from '../../components/filters/SidebarFilters'

const defaultFilters: FilterState = {
  departments: [],
  priceRange: '',
  rating: '',
  inStock: false,
  prime: false,
}

describe('SidebarFilters', () => {
  it('renders Filters heading', () => {
    render(<SidebarFilters filters={defaultFilters} onChange={vi.fn()} />)
    expect(screen.getByText('Filters')).toBeInTheDocument()
  })

  it('renders all department checkboxes', () => {
    render(<SidebarFilters filters={defaultFilters} onChange={vi.fn()} />)
    expect(screen.getByLabelText(/Electronics/)).toBeInTheDocument()
    expect(screen.getByLabelText(/Clothing/)).toBeInTheDocument()
    expect(screen.getByLabelText(/Books/)).toBeInTheDocument()
  })

  it('calls onChange when a department is checked', () => {
    const onChange = vi.fn()
    render(<SidebarFilters filters={defaultFilters} onChange={onChange} />)
    fireEvent.click(screen.getByLabelText(/Electronics/))
    expect(onChange).toHaveBeenCalledWith(
      expect.objectContaining({ departments: ['Electronics'] })
    )
  })

  it('removes department when unchecked', () => {
    const onChange = vi.fn()
    const filters = { ...defaultFilters, departments: ['Electronics'] }
    render(<SidebarFilters filters={filters} onChange={onChange} />)
    fireEvent.click(screen.getByLabelText(/Electronics/))
    expect(onChange).toHaveBeenCalledWith(
      expect.objectContaining({ departments: [] })
    )
  })

  it('calls onChange with priceRange when radio selected', () => {
    const onChange = vi.fn()
    render(<SidebarFilters filters={defaultFilters} onChange={onChange} />)
    fireEvent.click(screen.getByLabelText(/Under \$25/))
    expect(onChange).toHaveBeenCalledWith(
      expect.objectContaining({ priceRange: 'under25' })
    )
  })

  it('calls onChange with inStock true when checked', () => {
    const onChange = vi.fn()
    render(<SidebarFilters filters={defaultFilters} onChange={onChange} />)
    fireEvent.click(screen.getByLabelText(/In Stock/))
    expect(onChange).toHaveBeenCalledWith(
      expect.objectContaining({ inStock: true })
    )
  })

  it('Clear Filters resets all filters', () => {
    const onChange = vi.fn()
    const filters: FilterState = {
      departments: ['Electronics'],
      priceRange: 'under25',
      rating: '4',
      inStock: true,
      prime: true,
    }
    render(<SidebarFilters filters={filters} onChange={onChange} />)
    fireEvent.click(screen.getByText('Clear Filters'))
    expect(onChange).toHaveBeenCalledWith({
      departments: [],
      priceRange: '',
      rating: '',
      inStock: false,
      prime: false,
    })
  })
})
