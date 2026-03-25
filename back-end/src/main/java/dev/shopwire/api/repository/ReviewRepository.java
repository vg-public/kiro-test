package dev.shopwire.api.repository;

import dev.shopwire.api.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Page<Review> findByProductProductId(UUID productId, Pageable pageable);
    Optional<Review> findByProductProductIdAndUserUserId(UUID productId, UUID userId);
    boolean existsByProductProductIdAndUserUserId(UUID productId, UUID userId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.productId = :productId AND r.rating = :rating")
    long countByProductIdAndRating(@Param("productId") UUID productId, @Param("rating") short rating);
}
