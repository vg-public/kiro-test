package dev.shopwire.api.dto.cart;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemDto(
        int cart_item_id,
        UUID variant_id,
        UUID product_id,
        String title,
        String variant_title,
        String image_url,
        BigDecimal unit_price,
        int quantity,
        BigDecimal line_total,
        boolean is_prime
) {}
