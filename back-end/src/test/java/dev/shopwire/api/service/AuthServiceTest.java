package dev.shopwire.api.service;

import dev.shopwire.api.config.JwtConfig;
import dev.shopwire.api.dto.auth.AuthResponse;
import dev.shopwire.api.dto.auth.LoginRequest;
import dev.shopwire.api.dto.auth.RegisterRequest;
import dev.shopwire.api.entity.User;
import dev.shopwire.api.exception.ApiException;
import dev.shopwire.api.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtConfig jwtConfig;
    @Mock DtoMapper mapper;
    @Mock HttpServletResponse response;

    @InjectMocks AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(UUID.randomUUID())
                .email("test@example.com")
                .passwordHash("$2a$10$hashed")
                .firstName("John")
                .lastName("Doe")
                .active(true)
                .verified(false)
                .build();
    }

    @Test
    void register_success() {
        RegisterRequest req = new RegisterRequest("new@example.com", "password123", "Jane", "Doe", null);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtConfig.generateAccessToken(any(), any())).thenReturn("access-token");
        when(jwtConfig.generateRefreshToken(any())).thenReturn("refresh-token");
        when(jwtConfig.getExpirySeconds()).thenReturn(900L);
        when(mapper.toUserProfile(any())).thenReturn(null);

        AuthResponse result = authService.register(req, response);

        assertThat(result.access_token()).isEqualTo("access-token");
        assertThat(result.token_type()).isEqualTo("Bearer");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_emailTaken_throws409() {
        RegisterRequest req = new RegisterRequest("test@example.com", "password123", "Jane", "Doe", null);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(req, response))
                .isInstanceOf(ApiException.class)
                .satisfies(e -> assertThat(((ApiException) e).getStatus()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void login_success() {
        LoginRequest req = new LoginRequest("test@example.com", "password123");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPasswordHash())).thenReturn(true);
        when(jwtConfig.generateAccessToken(any(), any())).thenReturn("access-token");
        when(jwtConfig.generateRefreshToken(any())).thenReturn("refresh-token");
        when(jwtConfig.getExpirySeconds()).thenReturn(900L);
        when(mapper.toUserProfile(any())).thenReturn(null);

        AuthResponse result = authService.login(req, response);

        assertThat(result.access_token()).isEqualTo("access-token");
    }

    @Test
    void login_wrongPassword_throws401() {
        LoginRequest req = new LoginRequest("test@example.com", "wrongpass");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpass", testUser.getPasswordHash())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(req, response))
                .isInstanceOf(ApiException.class)
                .satisfies(e -> assertThat(((ApiException) e).getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED));
    }

    @Test
    void login_userNotFound_throws401() {
        LoginRequest req = new LoginRequest("nobody@example.com", "pass");
        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(req, response))
                .isInstanceOf(ApiException.class)
                .satisfies(e -> assertThat(((ApiException) e).getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED));
    }
}
