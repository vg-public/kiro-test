import { useNavigate } from 'react-router-dom'

const NAV_LINKS = [
  { label: "Today's Deals", category: '' },
  { label: 'Electronics', category: 'electronics' },
  { label: 'Clothing', category: 'clothing' },
  { label: 'Books', category: 'books' },
  { label: 'Home & Garden', category: 'home-garden' },
  { label: 'Sports', category: 'sports' },
  { label: 'Toys', category: 'toys' },
  { label: 'Automotive', category: 'automotive' },
]

export default function SubNav() {
  const navigate = useNavigate()

  return (
    <nav className="subnav">
      {NAV_LINKS.map((link) => (
        <a
          key={link.label}
          href="#"
          onClick={(e) => {
            e.preventDefault()
            navigate(link.category ? `/products?category=${link.category}` : '/products')
          }}
        >
          {link.label}
        </a>
      ))}
    </nav>
  )
}
