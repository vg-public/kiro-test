package dev.shopwire.api.dto.order;

import dev.shopwire.api.dto.user.AddressDto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record OrderDetailDto(
        UUID order_id,
        String status,
        BigDecimal total_amount,
        String currency,
        int item_count,
        OffsetDateTime created_at,
        AddressDto shipping_address,
        BigDecimal subtotal,
        BigDecimal shipping_cost,
        BigDecimal tax_amount,
        BigDecimal discount_amount,
        String notes,
        List<OrderItemDto> items,
        List<ShipmentDto> shipments
) {}
