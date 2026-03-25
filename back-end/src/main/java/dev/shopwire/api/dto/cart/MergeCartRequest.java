package dev.shopwire.api.dto.cart;

import jakarta.validation.constraints.NotBlank;

public record MergeCartRequest(@NotBlank String session_id) {}
