package dev.shopwire.api.service;

import dev.shopwire.api.dto.cart.AddToCartRequest;
import dev.shopwire.api.dto.cart.CartDto;
import dev.shopwire.api.dto.cart.MergeCartRequest;
import dev.shopwire.api.entity.*;
import dev.shopwire.api.exception.ApiException;
import dev.shopwire.api.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock CartRepository cartRepository;
    @Mock CartItemRepository cartItemRepository;
    @Mock ProductVariantRepository variantRepository;
    @Mock UserRepository userRepository;
    @Mock DtoMapper mapper;

    @InjectMocks CartService cartService;

    private UUID userId;
    private UUID variantId;
    private ProductVariant variant;
    private Cart cart;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        variantId = UUID.randomUUID();

        user = User.builder().userId(userId).build();

        Product product = Product.builder()
                .productId(UUID.randomUUID())
                .title("Test Product")
                .prime(false)
                .images(new ArrayList<>())
                .build();

        variant = ProductVariant.builder()
                .variantId(variantId)
                .product(product)
                .title("Default")
                .price(BigDecimal.valueOf(29.99))
                .stockQty(10)
                .active(true)
                .attributeValues(new ArrayList<>())
                .build();

        cart = Cart.builder()
                .cartId(UUID.randomUUID())
                .user(user)
                .items(new ArrayList<>())
                .build();
    }

    @Test
    void addItem_newItem_success() {
        AddToCartRequest req = new AddToCartRequest(variantId, 2);
        when(variantRepository.findByVariantIdAndActiveTrue(variantId)).thenReturn(Optional.of(variant));
        when(cartRepository.findByUserUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartCartIdAndVariantVariantId(cart.getCartId(), variantId))
                .thenReturn(Optional.empty());
        when(cartItemRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(cartRepository.findById(cart.getCartId())).thenReturn(Optional.of(cart));
        when(mapper.toCartDto(any())).thenReturn(new CartDto(cart.getCartId(), List.of(), BigDecimal.ZERO, 0));

        CartDto result = cartService.addItem(userId, req);

        assertThat(result).isNotNull();
        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    void addItem_insufficientStock_throws409() {
        AddToCartRequest req = new AddToCartRequest(variantId, 20);
        variant.setStockQty(5);
        when(variantRepository.findByVariantIdAndActiveTrue(variantId)).thenReturn(Optional.of(variant));

        assertThatThrownBy(() -> cartService.addItem(userId, req))
                .isInstanceOf(ApiException.class)
                .satisfies(e -> assertThat(((ApiException) e).getStatus()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void mergeCart_mergesGuestItemsIntoUserCart() {
        UUID guestCartId = UUID.randomUUID();
        Cart guestCart = Cart.builder()
                .cartId(guestCartId)
                .sessionId("session-123")
                .items(new ArrayList<>())
                .build();

        CartItem guestItem = CartItem.builder()
                .cartItemId(1)
                .cart(guestCart)
                .variant(variant)
                .quantity(3)
                .build();
        guestCart.getItems().add(guestItem);

        MergeCartRequest req = new MergeCartRequest("session-123");
        when(cartRepository.findByUserUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.findBySessionId("session-123")).thenReturn(Optional.of(guestCart));
        when(cartItemRepository.findByCartCartIdAndVariantVariantId(cart.getCartId(), variantId))
                .thenReturn(Optional.empty());
        when(cartItemRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(cartRepository.findById(cart.getCartId())).thenReturn(Optional.of(cart));
        when(mapper.toCartDto(any())).thenReturn(new CartDto(cart.getCartId(), List.of(), BigDecimal.ZERO, 0));

        CartDto result = cartService.mergeCart(userId, req);

        assertThat(result).isNotNull();
        verify(cartRepository).delete(guestCart);
    }

    @Test
    void mergeCart_noGuestCart_returnsUserCart() {
        MergeCartRequest req = new MergeCartRequest("nonexistent-session");
        when(cartRepository.findByUserUserId(userId)).thenReturn(Optional.of(cart));
        when(cartRepository.findBySessionId("nonexistent-session")).thenReturn(Optional.empty());
        when(mapper.toCartDto(cart)).thenReturn(new CartDto(cart.getCartId(), List.of(), BigDecimal.ZERO, 0));

        CartDto result = cartService.mergeCart(userId, req);

        assertThat(result).isNotNull();
        verify(cartRepository, never()).delete(any(Cart.class));
    }
}
