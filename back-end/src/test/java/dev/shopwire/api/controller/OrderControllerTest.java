package dev.shopwire.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shopwire.api.config.JwtConfig;
import dev.shopwire.api.dto.order.OrderDetailDto;
import dev.shopwire.api.dto.order.OrderListResponse;
import dev.shopwire.api.dto.order.PlaceOrderRequest;
import dev.shopwire.api.dto.PaginationDto;
import dev.shopwire.api.exception.ApiException;
import dev.shopwire.api.security.JwtAuthFilter;
import dev.shopwire.api.security.ShopWireUserDetailsService;
import dev.shopwire.api.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean OrderService orderService;
    @MockBean JwtConfig jwtConfig;
    @MockBean JwtAuthFilter jwtAuthFilter;
    @MockBean ShopWireUserDetailsService userDetailsService;

    @Test
    @WithMockUser(username = "00000000-0000-0000-0000-000000000001")
    void listOrders_returns200() throws Exception {
        OrderListResponse response = new OrderListResponse(new PaginationDto(1, 20, 0, 0), List.of());
        when(orderService.listOrders(any(), anyInt(), anyInt(), any())).thenReturn(response);

        mockMvc.perform(get("/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orders").isArray());
    }

    @Test
    @WithMockUser(username = "00000000-0000-0000-0000-000000000001")
    void placeOrder_validRequest_returns201() throws Exception {
        UUID addressId = UUID.randomUUID();
        PlaceOrderRequest req = new PlaceOrderRequest(addressId, null, null);
        OrderDetailDto detail = new OrderDetailDto(
                UUID.randomUUID(), "pending", BigDecimal.valueOf(108), "USD", 2,
                OffsetDateTime.now(), null, BigDecimal.valueOf(100), BigDecimal.ZERO,
                BigDecimal.valueOf(8), BigDecimal.ZERO, null, List.of(), List.of()
        );
        when(orderService.placeOrder(any(), any())).thenReturn(detail);

        mockMvc.perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("pending"));
    }

    @Test
    @WithMockUser(username = "00000000-0000-0000-0000-000000000001")
    void cancelOrder_conflict_returns409() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(orderService.cancelOrder(any(), eq(orderId)))
                .thenThrow(new ApiException(HttpStatus.CONFLICT, "CANNOT_CANCEL", "Order cannot be cancelled"));

        mockMvc.perform(post("/v1/orders/" + orderId + "/cancel"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CANNOT_CANCEL"));
    }
}
