package dev.shopwire.api.dto.order;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PlaceOrderRequest(
        @NotNull UUID address_id,
        String coupon_code,
        String notes
) {}
