package dev.shopwire.api.dto.search;

import dev.shopwire.api.dto.PaginationDto;
import dev.shopwire.api.dto.catalog.ProductSummaryDto;

import java.math.BigDecimal;
import java.util.List;

public record SearchResponse(
        String query,
        PaginationDto pagination,
        List<ProductSummaryDto> results,
        Facets facets
) {
    public record Facets(
            List<FacetItem> categories,
            List<FacetItem> brands,
            PriceRange price_range
    ) {}

    public record FacetItem(String name, long count) {}

    public record PriceRange(BigDecimal min, BigDecimal max) {}
}
