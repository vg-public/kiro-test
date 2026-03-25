package dev.shopwire.api.dto.catalog;

import dev.shopwire.api.dto.PaginationDto;

import java.util.List;
import java.util.Map;

public record ReviewsResponse(
        PaginationDto pagination,
        Map<Integer, Long> rating_breakdown,
        List<ReviewDto> reviews
) {}
