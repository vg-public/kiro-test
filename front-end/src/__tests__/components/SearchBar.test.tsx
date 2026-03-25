import { describe, it, expect, vi } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import SearchBar from '../../components/search/SearchBar'

const mockNavigate = vi.fn()
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom')
  return { ...actual, useNavigate: () => mockNavigate }
})

function renderSearchBar() {
  return render(
    <MemoryRouter>
      <SearchBar />
    </MemoryRouter>
  )
}

describe('SearchBar', () => {
  it('renders search input', () => {
    renderSearchBar()
    expect(screen.getByLabelText('Search products')).toBeInTheDocument()
  })

  it('renders category select', () => {
    renderSearchBar()
    expect(screen.getByLabelText('Search category')).toBeInTheDocument()
  })

  it('renders search button', () => {
    renderSearchBar()
    expect(screen.getByLabelText('Search')).toBeInTheDocument()
  })

  it('navigates to /search on button click with query', () => {
    renderSearchBar()
    fireEvent.change(screen.getByLabelText('Search products'), {
      target: { value: 'laptop' },
    })
    fireEvent.click(screen.getByLabelText('Search'))
    expect(mockNavigate).toHaveBeenCalledWith('/search?q=laptop')
  })

  it('navigates on Enter key press', () => {
    renderSearchBar()
    const input = screen.getByLabelText('Search products')
    fireEvent.change(input, { target: { value: 'headphones' } })
    fireEvent.keyDown(input, { key: 'Enter' })
    expect(mockNavigate).toHaveBeenCalledWith('/search?q=headphones')
  })

  it('does not navigate when query is empty', () => {
    mockNavigate.mockClear()
    renderSearchBar()
    fireEvent.click(screen.getByLabelText('Search'))
    expect(mockNavigate).not.toHaveBeenCalled()
  })

  it('includes category in search params when non-default selected', () => {
    renderSearchBar()
    fireEvent.change(screen.getByLabelText('Search category'), {
      target: { value: 'Electronics' },
    })
    fireEvent.change(screen.getByLabelText('Search products'), {
      target: { value: 'keyboard' },
    })
    fireEvent.click(screen.getByLabelText('Search'))
    expect(mockNavigate).toHaveBeenCalledWith(
      expect.stringContaining('category=electronics')
    )
  })
})
