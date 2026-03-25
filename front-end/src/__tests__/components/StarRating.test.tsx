import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import StarRating from '../../components/common/StarRating'

describe('StarRating', () => {
  it('renders correct number of stars', () => {
    const { container } = render(<StarRating rating={3} />)
    const stars = container.querySelectorAll('.star')
    expect(stars).toHaveLength(5)
  })

  it('marks correct stars as filled', () => {
    const { container } = render(<StarRating rating={4} />)
    const filled = container.querySelectorAll('.star:not(.empty)')
    const empty = container.querySelectorAll('.star.empty')
    expect(filled).toHaveLength(4)
    expect(empty).toHaveLength(1)
  })

  it('has accessible aria-label', () => {
    render(<StarRating rating={3} />)
    expect(screen.getByLabelText('3 out of 5 stars')).toBeInTheDocument()
  })

  it('renders 0 filled stars for rating 0', () => {
    const { container } = render(<StarRating rating={0} />)
    const empty = container.querySelectorAll('.star.empty')
    expect(empty).toHaveLength(5)
  })

  it('respects custom maxStars', () => {
    const { container } = render(<StarRating rating={2} maxStars={3} />)
    const stars = container.querySelectorAll('.star')
    expect(stars).toHaveLength(3)
  })
})
