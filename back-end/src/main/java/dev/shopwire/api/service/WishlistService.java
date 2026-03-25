package dev.shopwire.api.service;

import dev.shopwire.api.dto.wishlist.AddToWishlistRequest;
import dev.shopwire.api.dto.wishlist.WishlistItemDto;
import dev.shopwire.api.entity.*;
import dev.shopwire.api.exception.ApiException;
import dev.shopwire.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductVariantRepository variantRepository;
    private final UserRepository userRepository;
    private final DtoMapper mapper;

    public List<WishlistItemDto> getWishlist(UUID userId) {
        return wishlistRepository.findByUserUserId(userId).stream()
                .map(mapper::toWishlistItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public WishlistItemDto addToWishlist(UUID userId, AddToWishlistRequest req) {
        ProductVariant variant = variantRepository.findById(req.variant_id())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Variant not found"));

        wishlistRepository.findByUserUserIdAndVariantVariantId(userId, req.variant_id())
                .ifPresent(w -> { throw new ApiException(HttpStatus.CONFLICT, "ALREADY_IN_WISHLIST", "Already in wishlist"); });

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "User not found"));

        Wishlist wishlist = Wishlist.builder().user(user).variant(variant).build();
        return mapper.toWishlistItemDto(wishlistRepository.save(wishlist));
    }

    @Transactional
    public void removeFromWishlist(UUID userId, UUID variantId) {
        wishlistRepository.findByUserUserIdAndVariantVariantId(userId, variantId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Wishlist item not found"));
        wishlistRepository.deleteByUserUserIdAndVariantVariantId(userId, variantId);
    }
}
