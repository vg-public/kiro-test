package dev.shopwire.api.service;

import dev.shopwire.api.dto.coupon.CouponValidateRequest;
import dev.shopwire.api.dto.coupon.CouponValidateResponse;
import dev.shopwire.api.entity.Coupon;
import dev.shopwire.api.repository.CouponRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock CouponRepository couponRepository;
    @InjectMocks CouponService couponService;

    private Coupon buildCoupon(Coupon.DiscountType type, BigDecimal value) {
        return Coupon.builder()
                .couponId(1)
                .code("SAVE10")
                .discountType(type)
                .discountValue(value)
                .minOrderValue(BigDecimal.ZERO)
                .usedCount(0)
                .validFrom(OffsetDateTime.now().minusDays(1))
                .active(true)
                .build();
    }

    @Test
    void validate_validPercentageCoupon_returnsValid() {
        Coupon coupon = buildCoupon(Coupon.DiscountType.percentage, BigDecimal.valueOf(10));
        when(couponRepository.findByCodeIgnoreCase("SAVE10")).thenReturn(Optional.of(coupon));

        CouponValidateResponse result = couponService.validate(new CouponValidateRequest("SAVE10", BigDecimal.valueOf(100)));

        assertThat(result.valid()).isTrue();
        assertThat(result.discount_type()).isEqualTo("percentage");
        assertThat(result.discount_value()).isEqualByComparingTo(BigDecimal.valueOf(10));
    }

    @Test
    void validate_notFound_returnsInvalid() {
        when(couponRepository.findByCodeIgnoreCase("FAKE")).thenReturn(Optional.empty());

        CouponValidateResponse result = couponService.validate(new CouponValidateRequest("FAKE", BigDecimal.valueOf(50)));

        assertThat(result.valid()).isFalse();
    }

    @Test
    void validate_expiredCoupon_returnsInvalid() {
        Coupon coupon = buildCoupon(Coupon.DiscountType.fixed_amount, BigDecimal.valueOf(5));
        coupon.setValidUntil(OffsetDateTime.now().minusDays(1));
        when(couponRepository.findByCodeIgnoreCase("SAVE10")).thenReturn(Optional.of(coupon));

        CouponValidateResponse result = couponService.validate(new CouponValidateRequest("SAVE10", BigDecimal.valueOf(100)));

        assertThat(result.valid()).isFalse();
        assertThat(result.message()).contains("expired");
    }

    @Test
    void validate_minOrderNotMet_returnsInvalid() {
        Coupon coupon = buildCoupon(Coupon.DiscountType.fixed_amount, BigDecimal.valueOf(20));
        coupon.setMinOrderValue(BigDecimal.valueOf(100));
        when(couponRepository.findByCodeIgnoreCase("SAVE10")).thenReturn(Optional.of(coupon));

        CouponValidateResponse result = couponService.validate(new CouponValidateRequest("SAVE10", BigDecimal.valueOf(50)));

        assertThat(result.valid()).isFalse();
        assertThat(result.message()).contains("Minimum");
    }
}
