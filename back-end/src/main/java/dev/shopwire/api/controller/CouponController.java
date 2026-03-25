package dev.shopwire.api.controller;

import dev.shopwire.api.dto.coupon.CouponValidateRequest;
import dev.shopwire.api.dto.coupon.CouponValidateResponse;
import dev.shopwire.api.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/validate")
    public ResponseEntity<CouponValidateResponse> validate(@Valid @RequestBody CouponValidateRequest req) {
        return ResponseEntity.ok(couponService.validate(req));
    }
}
