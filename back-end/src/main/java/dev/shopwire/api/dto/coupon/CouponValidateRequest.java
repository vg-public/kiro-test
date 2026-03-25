package dev.shopwire.api.dto.coupon;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CouponValidateRequest(
        @NotBlank String code,
        BigDecimal cart_total
) {}
