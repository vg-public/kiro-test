package dev.shopwire.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shopwire.api.config.JwtConfig;
import dev.shopwire.api.dto.cart.AddToCartRequest;
import dev.shopwire.api.dto.cart.CartDto;
import dev.shopwire.api.security.JwtAuthFilter;
import dev.shopwire.api.security.ShopWireUserDetailsService;
import dev.shopwire.api.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean CartService cartService;
    @MockBean JwtConfig jwtConfig;
    @MockBean JwtAuthFilter jwtAuthFilter;
    @MockBean ShopWireUserDetailsService userDetailsService;

    private CartDto emptyCart() {
        return new CartDto(UUID.randomUUID(), List.of(), BigDecimal.ZERO, 0);
    }

    @Test
    @WithMockUser(username = "00000000-0000-0000-0000-000000000001")
    void getCart_authenticated_returns200() throws Exception {
        when(cartService.getCart(any())).thenReturn(emptyCart());

        mockMvc.perform(get("/v1/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    @WithMockUser(username = "00000000-0000-0000-0000-000000000001")
    void addItem_validRequest_returns200() throws Exception {
        AddToCartRequest req = new AddToCartRequest(UUID.randomUUID(), 1);
        when(cartService.addItem(any(), any())).thenReturn(emptyCart());

        mockMvc.perform(post("/v1/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "00000000-0000-0000-0000-000000000001")
    void removeItem_returns200() throws Exception {
        when(cartService.removeItem(any(), eq(1))).thenReturn(emptyCart());

        mockMvc.perform(delete("/v1/cart/items/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getCart_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/v1/cart"))
                .andExpect(status().isUnauthorized());
    }
}
