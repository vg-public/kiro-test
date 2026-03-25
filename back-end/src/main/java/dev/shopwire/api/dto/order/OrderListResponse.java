package dev.shopwire.api.dto.order;

import dev.shopwire.api.dto.PaginationDto;

import java.util.List;

public record OrderListResponse(PaginationDto pagination, List<OrderSummaryDto> orders) {}
