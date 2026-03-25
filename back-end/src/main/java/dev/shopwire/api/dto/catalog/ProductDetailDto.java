package dev.shopwire.api.dto.catalog;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProductDetailDto(
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
        boolean in_stock,
        String description,
        List<String> bullet_points,
        List<ImageDto> images,
        List<VariantDto> variants
) {
    public record ImageDto(String url, String alt_text, boolean is_primary) {}
}
