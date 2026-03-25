package dev.shopwire.api.repository;

import dev.shopwire.api.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByUserUserId(UUID userId);
    Optional<Cart> findBySessionId(String sessionId);
}
