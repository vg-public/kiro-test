package dev.shopwire.api.dto.cart;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartDto(
        UUID cart_id,
        List<CartItemDto> items,
        BigDecimal subtotal,
        int item_count
) {}
