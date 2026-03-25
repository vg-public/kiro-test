package dev.shopwire.api.repository;

import dev.shopwire.api.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

    Optional<Product> findByProductIdAndActiveTrue(UUID productId);

    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
           "to_tsvector('english', p.title) @@ plainto_tsquery('english', :query)")
    Page<Product> fullTextSearch(@Param("query") String query, Pageable pageable);

    @Query("SELECT DISTINCT p.title FROM Product p WHERE p.active = true AND " +
           "LOWER(p.title) LIKE LOWER(CONCAT(:prefix, '%')) ORDER BY p.title")
    java.util.List<String> findTitleSuggestions(@Param("prefix") String prefix, Pageable pageable);
}
