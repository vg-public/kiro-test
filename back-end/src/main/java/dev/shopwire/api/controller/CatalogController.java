package dev.shopwire.api.controller;

import dev.shopwire.api.dto.catalog.*;
import dev.shopwire.api.dto.search.SearchResponse;
import dev.shopwire.api.security.SecurityUtils;
import dev.shopwire.api.service.CatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    @GetMapping("/v1/categories")
    public ResponseEntity<List<CategoryDto>> getCategories() {
        return ResponseEntity.ok(catalogService.getCategories());
    }

    @GetMapping("/v1/products")
    public ResponseEntity<SearchResponse> listProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(name = "price_min", required = false) BigDecimal priceMin,
            @RequestParam(name = "price_max", required = false) BigDecimal priceMax,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Boolean prime,
            @RequestParam(name = "in_stock", required = false) Boolean inStock,
            @RequestParam(required = false) String badge,
            @RequestParam(defaultValue = "featured") String sort) {
        return ResponseEntity.ok(catalogService.listProducts(page, limit, category, brand,
                priceMin, priceMax, rating, prime, inStock, badge, sort));
    }

    @GetMapping("/v1/products/{productId}")
    public ResponseEntity<ProductDetailDto> getProduct(@PathVariable UUID productId) {
        return ResponseEntity.ok(catalogService.getProduct(productId));
    }

    @GetMapping("/v1/products/{productId}/reviews")
    public ResponseEntity<ReviewsResponse> getReviews(
            @PathVariable UUID productId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "helpful") String sort) {
        return ResponseEntity.ok(catalogService.getReviews(productId, page, limit, sort));
    }

    @PostMapping("/v1/products/{productId}/reviews")
    public ResponseEntity<ReviewDto> submitReview(@PathVariable UUID productId,
                                                   @Valid @RequestBody ReviewRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(catalogService.submitReview(productId, SecurityUtils.currentUserId(), req));
    }
}
