package dev.shopwire.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shopwire.api.config.JwtConfig;
import dev.shopwire.api.dto.catalog.CategoryDto;
import dev.shopwire.api.dto.catalog.ProductDetailDto;
import dev.shopwire.api.dto.search.SearchResponse;
import dev.shopwire.api.dto.PaginationDto;
import dev.shopwire.api.exception.ApiException;
import dev.shopwire.api.security.JwtAuthFilter;
import dev.shopwire.api.security.ShopWireUserDetailsService;
import dev.shopwire.api.service.CatalogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CatalogController.class)
class CatalogControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean CatalogService catalogService;
    @MockBean JwtConfig jwtConfig;
    @MockBean JwtAuthFilter jwtAuthFilter;
    @MockBean ShopWireUserDetailsService userDetailsService;

    @Test
    void getCategories_returns200() throws Exception {
        CategoryDto cat = new CategoryDto(1, null, "Electronics", "electronics", null, List.of());
        when(catalogService.getCategories()).thenReturn(List.of(cat));

        mockMvc.perform(get("/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Electronics"))
                .andExpect(jsonPath("$[0].slug").value("electronics"));
    }

    @Test
    void listProducts_returns200() throws Exception {
        SearchResponse response = new SearchResponse(null,
                new PaginationDto(1, 20, 0, 0), List.of(), null);
        when(catalogService.listProducts(anyInt(), anyInt(), any(), any(), any(), any(),
                any(), any(), any(), any(), any())).thenReturn(response);

        mockMvc.perform(get("/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results").isArray());
    }

    @Test
    void getProduct_found_returns200() throws Exception {
        UUID productId = UUID.randomUUID();
        ProductDetailDto detail = new ProductDetailDto(
                productId, "Test Product", "test-product", "Brand", "Category",
                null, BigDecimal.valueOf(99.99), null, "USD", null, null,
                BigDecimal.valueOf(4.5), 10, false, true,
                "Description", List.of(), List.of(), List.of()
        );
        when(catalogService.getProduct(productId)).thenReturn(detail);

        mockMvc.perform(get("/v1/products/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Product"));
    }

    @Test
    void getProduct_notFound_returns404() throws Exception {
        UUID productId = UUID.randomUUID();
        when(catalogService.getProduct(productId))
                .thenThrow(new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Product not found"));

        mockMvc.perform(get("/v1/products/" + productId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }
}
