package dev.shopwire.api.repository;

import dev.shopwire.api.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {
    List<Wishlist> findByUserUserId(UUID userId);
    Optional<Wishlist> findByUserUserIdAndVariantVariantId(UUID userId, UUID variantId);
    void deleteByUserUserIdAndVariantVariantId(UUID userId, UUID variantId);
}
