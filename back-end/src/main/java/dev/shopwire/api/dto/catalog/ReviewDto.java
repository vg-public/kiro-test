package dev.shopwire.api.dto.catalog;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ReviewDto(
        UUID review_id,
        String user_name,
        int rating,
        String title,
        String body,
        boolean is_verified,
        int helpful_count,
        OffsetDateTime created_at
) {}
