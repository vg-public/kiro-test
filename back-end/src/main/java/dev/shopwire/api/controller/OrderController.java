package dev.shopwire.api.controller;

import dev.shopwire.api.dto.order.OrderDetailDto;
import dev.shopwire.api.dto.order.OrderListResponse;
import dev.shopwire.api.dto.order.PlaceOrderRequest;
import dev.shopwire.api.security.SecurityUtils;
import dev.shopwire.api.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<OrderListResponse> listOrders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(orderService.listOrders(SecurityUtils.currentUserId(), page, limit, status));
    }

    @PostMapping
    public ResponseEntity<OrderDetailDto> placeOrder(@Valid @RequestBody PlaceOrderRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.placeOrder(SecurityUtils.currentUserId(), req));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailDto> getOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.getOrder(SecurityUtils.currentUserId(), orderId));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDetailDto> cancelOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(SecurityUtils.currentUserId(), orderId));
    }
}
