package dev.shopwire.api.dto.catalog;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductSummaryDto(
        UUID product_id,
        String title,
        String slug,
        String brand,
        String category,
        String primary_image,
        BigDecimal base_price,
        BigDecimal sale_price,
        String currency,
        Integer discount_pct,
        String badge,
        BigDecimal avg_rating,
        int review_count,
        boolean is_prime,
        boolean in_stock
) {}
