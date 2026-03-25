interface PaginationProps {
  page: number
  pages: number
  onPageChange: (page: number) => void
}

export default function Pagination({ page, pages, onPageChange }: PaginationProps) {
  if (pages <= 1) return null

  const getPageNumbers = () => {
    const nums: (number | '...')[] = []
    if (pages <= 7) {
      for (let i = 1; i <= pages; i++) nums.push(i)
    } else {
      nums.push(1)
      if (page > 3) nums.push('...')
      for (let i = Math.max(2, page - 1); i <= Math.min(pages - 1, page + 1); i++) {
        nums.push(i)
      }
      if (page < pages - 2) nums.push('...')
      nums.push(pages)
    }
    return nums
  }

  return (
    <div className="pagination">
      <button
        className="page-btn"
        onClick={() => onPageChange(page - 1)}
        disabled={page === 1}
      >
        ‹ Prev
      </button>
      {getPageNumbers().map((n, i) =>
        n === '...' ? (
          <span key={`ellipsis-${i}`} style={{ padding: '6px 4px', fontSize: 13 }}>…</span>
        ) : (
          <button
            key={n}
            className={`page-btn${n === page ? ' active' : ''}`}
            onClick={() => onPageChange(n as number)}
          >
            {n}
          </button>
        )
      )}
      <button
        className="page-btn"
        onClick={() => onPageChange(page + 1)}
        disabled={page === pages}
      >
        Next ›
      </button>
    </div>
  )
}
