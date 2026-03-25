package dev.shopwire.api.service;

import dev.shopwire.api.dto.coupon.CouponValidateRequest;
import dev.shopwire.api.dto.coupon.CouponValidateResponse;
import dev.shopwire.api.entity.Coupon;
import dev.shopwire.api.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponValidateResponse validate(CouponValidateRequest req) {
        Optional<Coupon> couponOpt = couponRepository.findByCodeIgnoreCase(req.code());

        if (couponOpt.isEmpty()) {
            return new CouponValidateResponse(false, null, null, "Coupon not found");
        }

        Coupon coupon = couponOpt.get();

        if (!coupon.isActive()) {
            return new CouponValidateResponse(false, null, null, "Coupon is not active");
        }

        OffsetDateTime now = OffsetDateTime.now();
        if (now.isBefore(coupon.getValidFrom())) {
            return new CouponValidateResponse(false, null, null, "Coupon is not yet valid");
        }
        if (coupon.getValidUntil() != null && now.isAfter(coupon.getValidUntil())) {
            return new CouponValidateResponse(false, null, null, "Coupon has expired");
        }
        if (coupon.getMaxUses() != null && coupon.getUsedCount() >= coupon.getMaxUses()) {
            return new CouponValidateResponse(false, null, null, "Coupon usage limit reached");
        }

        BigDecimal cartTotal = req.cart_total() != null ? req.cart_total() : BigDecimal.ZERO;
        if (cartTotal.compareTo(coupon.getMinOrderValue()) < 0) {
            return new CouponValidateResponse(false, null, null,
                    "Minimum order value of " + coupon.getMinOrderValue() + " required");
        }

        return new CouponValidateResponse(true, coupon.getDiscountType().name(),
                coupon.getDiscountValue(), "Coupon applied successfully");
    }
}
