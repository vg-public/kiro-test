package dev.shopwire.api.dto.catalog;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record VariantDto(
        UUID variant_id,
        String sku,
        String title,
        BigDecimal price,
        int stock_qty,
        boolean is_active,
        List<AttributeDto> attributes
) {
    public record AttributeDto(String name, String value) {}
}
