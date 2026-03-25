package dev.shopwire.api.repository;

import dev.shopwire.api.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {
    Optional<ProductVariant> findByVariantIdAndActiveTrue(UUID variantId);
}
