import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useSelector } from 'react-redux'
import type { RootState } from '../store'
import { useGetOrdersQuery, useCancelOrderMutation } from '../api/shopwireApi'
import type { OrderStatus } from '../types/api'
import Pagination from '../components/common/Pagination'
import ErrorMessage from '../components/common/ErrorMessage'

export default function OrdersPage() {
  const navigate = useNavigate()
  const { user } = useSelector((s: RootState) => s.auth)
  const [page, setPage] = useState(1)
  const [cancelOrder] = useCancelOrderMutation()

  const { data, isLoading, isError } = useGetOrdersQuery(
    { page, limit: 10 },
    { skip: !user }
  )

  if (!user) {
    return (
      <div className="orders-page">
        <ErrorMessage message="Please sign in to view your orders." />
        <button onClick={() => navigate('/auth')} className="submit-btn" style={{ marginTop: 16 }}>
          Sign In
        </button>
      </div>
    )
  }

  if (isLoading) {
    return (
      <div className="loading" style={{ marginTop: 48 }}>
        <div className="spinner" />
        <p>Loading orders…</p>
      </div>
    )
  }

  if (isError) {
    return (
      <div className="orders-page">
        <ErrorMessage message="Failed to load orders." />
      </div>
    )
  }

  return (
    <div className="orders-page">
      <h1>Your Orders</h1>

      {(!data || data.orders.length === 0) && (
        <div style={{ textAlign: 'center', padding: 48, color: '#555' }}>
          <p>You haven't placed any orders yet.</p>
          <button className="hero-btn" style={{ marginTop: 16 }} onClick={() => navigate('/products')}>
            Start Shopping
          </button>
        </div>
      )}

      {data?.orders.map((order) => (
        <div key={order.order_id} className="order-card">
          <div className="order-card-header">
            <div>
              <div className="order-id">Order #{order.order_id.slice(0, 8).toUpperCase()}</div>
              <div className="order-date">{new Date(order.created_at).toLocaleDateString()}</div>
            </div>
            <span className={`order-status ${order.status}`}>{order.status}</span>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <span style={{ fontSize: 13, color: '#555' }}>{order.item_count} item{order.item_count !== 1 ? 's' : ''}</span>
            </div>
            <div className="order-total">${order.total_amount.toFixed(2)}</div>
          </div>
          {(order.status === 'pending' || order.status === 'confirmed') && (
            <button
              onClick={() => cancelOrder(order.order_id)}
              style={{
                marginTop: 8,
                background: 'none',
                border: '1px solid #cc0c39',
                color: '#cc0c39',
                padding: '4px 12px',
                borderRadius: 4,
                cursor: 'pointer',
                fontSize: 13,
              }}
            >
              Cancel Order
            </button>
          )}
        </div>
      ))}

      {data && (
        <Pagination
          page={data.pagination.page}
          pages={data.pagination.pages}
          onPageChange={setPage}
        />
      )}
    </div>
  )
}

// Suppress unused import warning
const _: OrderStatus = 'pending'
void _
