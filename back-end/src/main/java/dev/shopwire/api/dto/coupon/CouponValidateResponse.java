package dev.shopwire.api.dto.coupon;

import java.math.BigDecimal;

public record CouponValidateResponse(
        boolean valid,
        String discount_type,
        BigDecimal discount_value,
        String message
) {}
