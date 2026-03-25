package dev.shopwire.api.dto.order;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ShipmentDto(
        UUID shipment_id,
        String carrier,
        String tracking_number,
        OffsetDateTime shipped_at,
        OffsetDateTime estimated_at,
        OffsetDateTime delivered_at
) {}
