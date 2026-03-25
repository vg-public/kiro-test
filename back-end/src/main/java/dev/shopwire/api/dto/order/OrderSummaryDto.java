package dev.shopwire.api.dto.order;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record OrderSummaryDto(
        UUID order_id,
        String status,
        BigDecimal total_amount,
        String currency,
        int item_count,
        OffsetDateTime created_at
) {}
