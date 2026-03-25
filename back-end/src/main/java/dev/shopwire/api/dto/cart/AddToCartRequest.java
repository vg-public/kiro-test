package dev.shopwire.api.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddToCartRequest(
        @NotNull UUID variant_id,
        @Min(1) int quantity
) {}
