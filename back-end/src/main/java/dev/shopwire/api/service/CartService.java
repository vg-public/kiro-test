package dev.shopwire.api.service;

import dev.shopwire.api.dto.cart.*;
import dev.shopwire.api.entity.*;
import dev.shopwire.api.exception.ApiException;
import dev.shopwire.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository variantRepository;
    private final UserRepository userRepository;
    private final DtoMapper mapper;

    public CartDto getCart(UUID userId) {
        Cart cart = getOrCreateCart(userId);
        return mapper.toCartDto(cart);
    }

    @Transactional
    public CartDto addItem(UUID userId, AddToCartRequest req) {
        ProductVariant variant = variantRepository.findByVariantIdAndActiveTrue(req.variant_id())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Variant not found"));

        if (variant.getStockQty() < req.quantity()) {
            throw new ApiException(HttpStatus.CONFLICT, "INSUFFICIENT_STOCK", "Not enough stock available");
        }

        Cart cart = getOrCreateCart(userId);

        Optional<CartItem> existing = cartItemRepository.findByCartCartIdAndVariantVariantId(
                cart.getCartId(), req.variant_id());

        if (existing.isPresent()) {
            CartItem item = existing.get();
            int newQty = item.getQuantity() + req.quantity();
            if (variant.getStockQty() < newQty) {
                throw new ApiException(HttpStatus.CONFLICT, "INSUFFICIENT_STOCK", "Not enough stock available");
            }
            item.setQuantity(newQty);
            cartItemRepository.save(item);
        } else {
            CartItem item = CartItem.builder()
                    .cart(cart)
                    .variant(variant)
                    .quantity(req.quantity())
                    .build();
            cart.getItems().add(item);
            cartItemRepository.save(item);
        }

        return mapper.toCartDto(cartRepository.findById(cart.getCartId()).orElse(cart));
    }

    @Transactional
    public CartDto updateItem(UUID userId, Integer cartItemId, UpdateCartItemRequest req) {
        CartItem item = cartItemRepository.findByCartItemIdAndCartUserUserId(cartItemId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Cart item not found"));

        if (item.getVariant().getStockQty() < req.quantity()) {
            throw new ApiException(HttpStatus.CONFLICT, "INSUFFICIENT_STOCK", "Not enough stock available");
        }
        item.setQuantity(req.quantity());
        cartItemRepository.save(item);

        Cart cart = cartRepository.findByUserUserId(userId).orElseThrow();
        return mapper.toCartDto(cart);
    }

    @Transactional
    public CartDto removeItem(UUID userId, Integer cartItemId) {
        CartItem item = cartItemRepository.findByCartItemIdAndCartUserUserId(cartItemId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Cart item not found"));
        Cart cart = item.getCart();
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
        return mapper.toCartDto(cartRepository.findById(cart.getCartId()).orElse(cart));
    }

    @Transactional
    public CartDto mergeCart(UUID userId, MergeCartRequest req) {
        Cart userCart = getOrCreateCart(userId);
        Optional<Cart> guestCartOpt = cartRepository.findBySessionId(req.session_id());

        if (guestCartOpt.isEmpty()) {
            return mapper.toCartDto(userCart);
        }

        Cart guestCart = guestCartOpt.get();
        for (CartItem guestItem : guestCart.getItems()) {
            Optional<CartItem> existing = cartItemRepository.findByCartCartIdAndVariantVariantId(
                    userCart.getCartId(), guestItem.getVariant().getVariantId());
            if (existing.isPresent()) {
                // Merge quantities, capped at stock
                CartItem userItem = existing.get();
                int merged = userItem.getQuantity() + guestItem.getQuantity();
                int stock = guestItem.getVariant().getStockQty();
                userItem.setQuantity(Math.min(merged, stock));
                cartItemRepository.save(userItem);
            } else {
                CartItem newItem = CartItem.builder()
                        .cart(userCart)
                        .variant(guestItem.getVariant())
                        .quantity(guestItem.getQuantity())
                        .build();
                userCart.getItems().add(newItem);
                cartItemRepository.save(newItem);
            }
        }
        cartRepository.delete(guestCart);

        return mapper.toCartDto(cartRepository.findById(userCart.getCartId()).orElse(userCart));
    }

    private Cart getOrCreateCart(UUID userId) {
        return cartRepository.findByUserUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "User not found"));
            Cart cart = Cart.builder().user(user).build();
            return cartRepository.save(cart);
        });
    }
}
