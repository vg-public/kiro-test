package dev.shopwire.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.shopwire.api.config.JwtConfig;
import dev.shopwire.api.dto.auth.AuthResponse;
import dev.shopwire.api.dto.auth.LoginRequest;
import dev.shopwire.api.dto.auth.RegisterRequest;
import dev.shopwire.api.exception.ApiException;
import dev.shopwire.api.security.JwtAuthFilter;
import dev.shopwire.api.security.ShopWireUserDetailsService;
import dev.shopwire.api.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean AuthService authService;
    @MockBean JwtConfig jwtConfig;
    @MockBean JwtAuthFilter jwtAuthFilter;
    @MockBean ShopWireUserDetailsService userDetailsService;

    @Test
    void register_validRequest_returns201() throws Exception {
        RegisterRequest req = new RegisterRequest("user@example.com", "password123", "John", "Doe", null);
        AuthResponse authResponse = new AuthResponse("token", "Bearer", 900L, null);
        when(authService.register(any(), any(HttpServletResponse.class))).thenReturn(authResponse);

        mockMvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.access_token").value("token"))
                .andExpect(jsonPath("$.token_type").value("Bearer"));
    }

    @Test
    void register_invalidEmail_returns400() throws Exception {
        RegisterRequest req = new RegisterRequest("not-an-email", "password123", "John", "Doe", null);

        mockMvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_emailTaken_returns409() throws Exception {
        RegisterRequest req = new RegisterRequest("taken@example.com", "password123", "John", "Doe", null);
        when(authService.register(any(), any(HttpServletResponse.class)))
                .thenThrow(new ApiException(HttpStatus.CONFLICT, "EMAIL_TAKEN", "Email already registered"));

        mockMvc.perform(post("/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EMAIL_TAKEN"));
    }

    @Test
    void login_validCredentials_returns200() throws Exception {
        LoginRequest req = new LoginRequest("user@example.com", "password123");
        AuthResponse authResponse = new AuthResponse("token", "Bearer", 900L, null);
        when(authService.login(any(), any(HttpServletResponse.class))).thenReturn(authResponse);

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value("token"));
    }

    @Test
    void login_invalidCredentials_returns401() throws Exception {
        LoginRequest req = new LoginRequest("user@example.com", "wrongpass");
        when(authService.login(any(), any(HttpServletResponse.class)))
                .thenThrow(new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid email or password"));

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    void logout_returns204() throws Exception {
        doNothing().when(authService).logout(any(HttpServletResponse.class));

        mockMvc.perform(post("/v1/auth/logout"))
                .andExpect(status().isNoContent());
    }
}
