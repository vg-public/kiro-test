package dev.shopwire.api.dto.wishlist;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddToWishlistRequest(@NotNull UUID variant_id) {}
