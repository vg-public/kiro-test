package dev.shopwire.api.repository;

import dev.shopwire.api.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    Page<Order> findByUserUserId(UUID userId, Pageable pageable);
    Page<Order> findByUserUserIdAndStatus(UUID userId, Order.OrderStatus status, Pageable pageable);
    Optional<Order> findByOrderIdAndUserUserId(UUID orderId, UUID userId);

    boolean existsByUserUserIdAndItemsVariantVariantId(UUID userId, UUID variantId);
}
