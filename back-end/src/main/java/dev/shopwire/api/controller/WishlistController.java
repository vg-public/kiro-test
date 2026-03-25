package dev.shopwire.api.controller;

import dev.shopwire.api.dto.wishlist.AddToWishlistRequest;
import dev.shopwire.api.dto.wishlist.WishlistItemDto;
import dev.shopwire.api.security.SecurityUtils;
import dev.shopwire.api.service.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<List<WishlistItemDto>> getWishlist() {
        return ResponseEntity.ok(wishlistService.getWishlist(SecurityUtils.currentUserId()));
    }

    @PostMapping
    public ResponseEntity<WishlistItemDto> addToWishlist(@Valid @RequestBody AddToWishlistRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(wishlistService.addToWishlist(SecurityUtils.currentUserId(), req));
    }

    @DeleteMapping("/{variantId}")
    public ResponseEntity<Void> removeFromWishlist(@PathVariable UUID variantId) {
        wishlistService.removeFromWishlist(SecurityUtils.currentUserId(), variantId);
        return ResponseEntity.noContent().build();
    }
}
