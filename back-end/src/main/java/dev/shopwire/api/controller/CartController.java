package dev.shopwire.api.controller;

import dev.shopwire.api.dto.cart.*;
import dev.shopwire.api.security.SecurityUtils;
import dev.shopwire.api.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartDto> getCart() {
        return ResponseEntity.ok(cartService.getCart(SecurityUtils.currentUserId()));
    }

    @PostMapping
    public ResponseEntity<CartDto> addItem(@Valid @RequestBody AddToCartRequest req) {
        return ResponseEntity.ok(cartService.addItem(SecurityUtils.currentUserId(), req));
    }

    @PatchMapping("/items/{cartItemId}")
    public ResponseEntity<CartDto> updateItem(@PathVariable Integer cartItemId,
                                               @Valid @RequestBody UpdateCartItemRequest req) {
        return ResponseEntity.ok(cartService.updateItem(SecurityUtils.currentUserId(), cartItemId, req));
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<CartDto> removeItem(@PathVariable Integer cartItemId) {
        return ResponseEntity.ok(cartService.removeItem(SecurityUtils.currentUserId(), cartItemId));
    }

    @PostMapping("/merge")
    public ResponseEntity<CartDto> mergeCart(@Valid @RequestBody MergeCartRequest req) {
        return ResponseEntity.ok(cartService.mergeCart(SecurityUtils.currentUserId(), req));
    }
}
