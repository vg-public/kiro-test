package dev.shopwire.api.repository;

import dev.shopwire.api.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    Optional<CartItem> findByCartItemIdAndCartUserUserId(Integer cartItemId, UUID userId);
    Optional<CartItem> findByCartCartIdAndVariantVariantId(UUID cartId, UUID variantId);
}
