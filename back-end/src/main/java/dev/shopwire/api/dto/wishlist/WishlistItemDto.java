package dev.shopwire.api.dto.wishlist;

import dev.shopwire.api.dto.catalog.ProductSummaryDto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record WishlistItemDto(
        int wishlist_id,
        ProductSummaryDto product,
        UUID variant_id,
        OffsetDateTime added_at
) {}
