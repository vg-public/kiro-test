package dev.shopwire.api.dto.order;

import java.math.BigDecimal;

public record OrderItemDto(
        int order_item_id,
        String product_title,
        String variant_title,
        BigDecimal unit_price,
        int quantity,
        BigDecimal line_total
) {}
