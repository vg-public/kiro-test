import { Routes, Route } from 'react-router-dom'
import { useSelector } from 'react-redux'
import type { RootState } from './store'
import Header from './components/layout/Header'
import SubNav from './components/layout/SubNav'
import Footer from './components/layout/Footer'
import CartDrawer from './components/cart/CartDrawer'
import HomePage from './pages/HomePage'
import ProductListPage from './pages/ProductListPage'
import ProductDetailPage from './pages/ProductDetailPage'
import SearchPage from './pages/SearchPage'
import CartPage from './pages/CartPage'
import CheckoutPage from './pages/CheckoutPage'
import OrdersPage from './pages/OrdersPage'
import AuthPage from './pages/AuthPage'

export default function App() {
  const { isOpen: cartOpen } = useSelector((s: RootState) => s.cart)

  return (
    <>
      <Header />
      <SubNav />

      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/products" element={<ProductListPage />} />
        <Route path="/products/:productId" element={<ProductDetailPage />} />
        <Route path="/search" element={<SearchPage />} />
        <Route path="/cart" element={<CartPage />} />
        <Route path="/checkout" element={<CheckoutPage />} />
        <Route path="/orders" element={<OrdersPage />} />
        <Route path="/auth" element={<AuthPage />} />
      </Routes>

      <Footer />

      {cartOpen && <CartDrawer />}
    </>
  )
}
