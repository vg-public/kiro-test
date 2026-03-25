package dev.shopwire.api.service;

import dev.shopwire.api.dto.order.OrderDetailDto;
import dev.shopwire.api.dto.order.PlaceOrderRequest;
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
class OrderServiceTest {

    @Mock OrderRepository orderRepository;
    @Mock CartRepository cartRepository;
    @Mock CartItemRepository cartItemRepository;
    @Mock UserAddressRepository addressRepository;
    @Mock CouponRepository couponRepository;
    @Mock ProductVariantRepository variantRepository;
    @Mock DtoMapper mapper;

    @InjectMocks OrderService orderService;

    private UUID userId;
    private UUID addressId;
    private Cart cart;
    private UserAddress address;
    private ProductVariant variant;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        addressId = UUID.randomUUID();

        User user = User.builder().userId(userId).build();

        Product product = Product.builder()
                .productId(UUID.randomUUID())
                .title("Test Product")
                .variants(new ArrayList<>())
                .images(new ArrayList<>())
                .build();

        variant = ProductVariant.builder()
                .variantId(UUID.randomUUID())
                .product(product)
                .title("Default")
                .price(BigDecimal.valueOf(50.00))
                .stockQty(10)
                .active(true)
                .attributeValues(new ArrayList<>())
                .build();
        product.getVariants().add(variant);

        CartItem cartItem = CartItem.builder()
                .cartItemId(1)
                .variant(variant)
                .quantity(2)
                .build();

        cart = Cart.builder()
                .cartId(UUID.randomUUID())
                .user(user)
                .items(new ArrayList<>(List.of(cartItem)))
                .build();
        cartItem.setCart(cart);

        address = UserAddress.builder()
                .addressId(addressId)
                .user(user)
                .fullName("John Doe")
                .line1("123 Main St")
                .city("Springfield")
                .state("IL")
                .postalCode("62701")
                .country("US")
                .build();
    }

    @Test
    void placeOrder_success_deductsInventory() {
        PlaceOrderRequest req = new PlaceOrderRequest(addressId, null, null);
        Order savedOrder = Order.builder()
                .orderId(UUID.randomUUID())
                .status(Order.OrderStatus.pending)
                .subtotal(BigDecimal.valueOf(100))
                .shippingCost(BigDecimal.ZERO)
                .taxAmount(BigDecimal.valueOf(8))
                .discountAmount(BigDecimal.ZERO)
                .totalAmount(BigDecimal.valueOf(108))
                .currency("USD")
                .items(new ArrayList<>())
                .shipments(new ArrayList<>())
                .shippingAddress(new HashMap<>())
                .build();

        when(cartRepository.findByUserUserId(userId)).thenReturn(Optional.of(cart));
        when(addressRepository.findByAddressIdAndUserUserId(addressId, userId)).thenReturn(Optional.of(address));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderRepository.findById(any())).thenReturn(Optional.of(savedOrder));
        when(variantRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(cartRepository.save(any())).thenReturn(cart);
        when(mapper.toOrderDetail(any())).thenReturn(mock(OrderDetailDto.class));

        OrderDetailDto result = orderService.placeOrder(userId, req);

        assertThat(result).isNotNull();
        verify(variantRepository).save(argThat(v -> ((ProductVariant) v).getStockQty() == 8));
    }

    @Test
    void placeOrder_emptyCart_throws400() {
        cart.getItems().clear();
        when(cartRepository.findByUserUserId(userId)).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> orderService.placeOrder(userId, new PlaceOrderRequest(addressId, null, null)))
                .isInstanceOf(ApiException.class)
                .satisfies(e -> assertThat(((ApiException) e).getStatus()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void cancelOrder_pendingOrder_succeeds() {
        Order order = Order.builder()
                .orderId(UUID.randomUUID())
                .status(Order.OrderStatus.pending)
                .items(new ArrayList<>(List.of(
                        OrderItem.builder().variant(variant).quantity(2).build()
                )))
                .shipments(new ArrayList<>())
                .shippingAddress(new HashMap<>())
                .subtotal(BigDecimal.valueOf(100))
                .shippingCost(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .totalAmount(BigDecimal.valueOf(100))
                .currency("USD")
                .build();

        when(orderRepository.findByOrderIdAndUserUserId(order.getOrderId(), userId))
                .thenReturn(Optional.of(order));
        when(variantRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(any())).thenReturn(order);
        when(mapper.toOrderDetail(any())).thenReturn(mock(OrderDetailDto.class));

        OrderDetailDto result = orderService.cancelOrder(userId, order.getOrderId());

        assertThat(result).isNotNull();
        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.cancelled);
        // Inventory restored
        verify(variantRepository).save(argThat(v -> ((ProductVariant) v).getStockQty() == 12));
    }

    @Test
    void cancelOrder_shippedOrder_throws409() {
        Order order = Order.builder()
                .orderId(UUID.randomUUID())
                .status(Order.OrderStatus.shipped)
                .items(new ArrayList<>())
                .build();

        when(orderRepository.findByOrderIdAndUserUserId(order.getOrderId(), userId))
                .thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(userId, order.getOrderId()))
                .isInstanceOf(ApiException.class)
                .satisfies(e -> assertThat(((ApiException) e).getStatus()).isEqualTo(HttpStatus.CONFLICT));
    }
}
