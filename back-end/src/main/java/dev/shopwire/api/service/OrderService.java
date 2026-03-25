package dev.shopwire.api.service;

import dev.shopwire.api.dto.order.*;
import dev.shopwire.api.dto.PaginationDto;
import dev.shopwire.api.entity.*;
import dev.shopwire.api.exception.ApiException;
import dev.shopwire.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserAddressRepository addressRepository;
    private final CouponRepository couponRepository;
    private final ProductVariantRepository variantRepository;
    private final DtoMapper mapper;

    public OrderListResponse listOrders(UUID userId, int page, int limit, String status) {
        PageRequest pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> orderPage;
        if (status != null) {
            Order.OrderStatus orderStatus;
            try {
                orderStatus = Order.OrderStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_STATUS", "Invalid order status");
            }
            orderPage = orderRepository.findByUserUserIdAndStatus(userId, orderStatus, pageable);
        } else {
            orderPage = orderRepository.findByUserUserId(userId, pageable);
        }
        List<OrderSummaryDto> orders = orderPage.getContent().stream()
                .map(mapper::toOrderSummary)
                .collect(Collectors.toList());
        PaginationDto pagination = new PaginationDto(page, limit, orderPage.getTotalElements(), orderPage.getTotalPages());
        return new OrderListResponse(pagination, orders);
    }

    @Transactional
    public OrderDetailDto placeOrder(UUID userId, PlaceOrderRequest req) {
        Cart cart = cartRepository.findByUserUserId(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "CART_EMPTY", "Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "CART_EMPTY", "Cart is empty");
        }

        UserAddress address = addressRepository.findByAddressIdAndUserUserId(req.address_id(), userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Address not found"));

        // Validate stock and compute subtotal
        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem ci : cart.getItems()) {
            ProductVariant variant = ci.getVariant();
            if (variant.getStockQty() < ci.getQuantity()) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "INSUFFICIENT_STOCK",
                        "Insufficient stock for: " + variant.getTitle());
            }
            BigDecimal lineTotal = variant.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity()));
            subtotal = subtotal.add(lineTotal);

            OrderItem oi = OrderItem.builder()
                    .variant(variant)
                    .productTitle(variant.getProduct().getTitle())
                    .variantTitle(variant.getTitle())
                    .unitPrice(variant.getPrice())
                    .quantity(ci.getQuantity())
                    .lineTotal(lineTotal)
                    .build();
            orderItems.add(oi);
        }

        // Apply coupon
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (req.coupon_code() != null && !req.coupon_code().isBlank()) {
            discountAmount = applyCoupon(req.coupon_code(), subtotal);
        }

        BigDecimal shippingCost = BigDecimal.ZERO; // simplified
        BigDecimal taxAmount = subtotal.multiply(BigDecimal.valueOf(0.08)).setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal totalAmount = subtotal.add(shippingCost).add(taxAmount).subtract(discountAmount);

        // Snapshot address
        Map<String, Object> addrSnapshot = new LinkedHashMap<>();
        addrSnapshot.put("address_id", address.getAddressId().toString());
        addrSnapshot.put("label", address.getLabel());
        addrSnapshot.put("full_name", address.getFullName());
        addrSnapshot.put("line1", address.getLine1());
        addrSnapshot.put("line2", address.getLine2());
        addrSnapshot.put("city", address.getCity());
        addrSnapshot.put("state", address.getState());
        addrSnapshot.put("postal_code", address.getPostalCode());
        addrSnapshot.put("country", address.getCountry());
        addrSnapshot.put("is_default", address.isDefault());

        User user = new User();
        user.setUserId(userId);

        Order order = Order.builder()
                .user(user)
                .shippingAddress(addrSnapshot)
                .subtotal(subtotal)
                .shippingCost(shippingCost)
                .taxAmount(taxAmount)
                .discountAmount(discountAmount)
                .totalAmount(totalAmount)
                .notes(req.notes())
                .build();
        order = orderRepository.save(order);

        // Save order items and deduct inventory
        for (OrderItem oi : orderItems) {
            oi.setOrder(order);
            order.getItems().add(oi);

            ProductVariant variant = oi.getVariant();
            variant.setStockQty(variant.getStockQty() - oi.getQuantity());
            variantRepository.save(variant);
        }
        orderRepository.save(order);

        // Clear cart
        cart.getItems().clear();
        cartRepository.save(cart);

        return mapper.toOrderDetail(orderRepository.findById(order.getOrderId()).orElse(order));
    }

    public OrderDetailDto getOrder(UUID userId, UUID orderId) {
        Order order = orderRepository.findByOrderIdAndUserUserId(orderId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Order not found"));
        return mapper.toOrderDetail(order);
    }

    @Transactional
    public OrderDetailDto cancelOrder(UUID userId, UUID orderId) {
        Order order = orderRepository.findByOrderIdAndUserUserId(orderId, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Order not found"));

        if (order.getStatus() != Order.OrderStatus.pending && order.getStatus() != Order.OrderStatus.confirmed) {
            throw new ApiException(HttpStatus.CONFLICT, "CANNOT_CANCEL",
                    "Order cannot be cancelled at status: " + order.getStatus());
        }

        // Restore inventory
        for (OrderItem oi : order.getItems()) {
            ProductVariant variant = oi.getVariant();
            variant.setStockQty(variant.getStockQty() + oi.getQuantity());
            variantRepository.save(variant);
        }

        order.setStatus(Order.OrderStatus.cancelled);
        return mapper.toOrderDetail(orderRepository.save(order));
    }

    private BigDecimal applyCoupon(String code, BigDecimal subtotal) {
        Coupon coupon = couponRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "INVALID_COUPON", "Coupon not found"));

        if (!coupon.isActive()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "COUPON_INACTIVE", "Coupon is not active");
        }
        if (subtotal.compareTo(coupon.getMinOrderValue()) < 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "COUPON_MIN_NOT_MET",
                    "Minimum order value not met for this coupon");
        }
        if (coupon.getMaxUses() != null && coupon.getUsedCount() >= coupon.getMaxUses()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "COUPON_EXHAUSTED", "Coupon usage limit reached");
        }

        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponRepository.save(coupon);

        return switch (coupon.getDiscountType()) {
            case percentage -> subtotal.multiply(coupon.getDiscountValue().divide(BigDecimal.valueOf(100)));
            case fixed_amount -> coupon.getDiscountValue().min(subtotal);
            case free_shipping -> BigDecimal.ZERO; // shipping is already 0 in MVP
        };
    }
}
