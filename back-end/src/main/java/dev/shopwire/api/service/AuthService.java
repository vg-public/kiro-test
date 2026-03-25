package dev.shopwire.api.service;

import dev.shopwire.api.config.JwtConfig;
import dev.shopwire.api.dto.auth.AuthResponse;
import dev.shopwire.api.dto.auth.LoginRequest;
import dev.shopwire.api.dto.auth.RegisterRequest;
import dev.shopwire.api.entity.User;
import dev.shopwire.api.exception.ApiException;
import dev.shopwire.api.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtConfig jwtConfig;
    private final DtoMapper mapper;

    @Transactional
    public AuthResponse register(RegisterRequest req, HttpServletResponse response) {
        if (userRepository.existsByEmail(req.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "EMAIL_TAKEN", "Email already registered");
        }
        User user = User.builder()
                .email(req.email())
                .passwordHash(passwordEncoder.encode(req.password()))
                .firstName(req.first_name())
                .lastName(req.last_name())
                .phone(req.phone())
                .build();
        userRepository.save(user);
        return buildAuthResponse(user, response);
    }

    public AuthResponse login(LoginRequest req, HttpServletResponse response) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid email or password"));
        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS", "Invalid email or password");
        }
        if (!user.isActive()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "ACCOUNT_DISABLED", "Account is disabled");
        }
        return buildAuthResponse(user, response);
    }

    public AuthResponse refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshCookie(request);
        if (refreshToken == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "NO_REFRESH_TOKEN", "Refresh token missing");
        }
        try {
            Claims claims = jwtConfig.parseToken(refreshToken);
            if (!jwtConfig.isRefreshToken(claims)) {
                throw new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "Not a refresh token");
            }
            UUID userId = UUID.fromString(claims.getSubject());
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "USER_NOT_FOUND", "User not found"));
            return buildAuthResponse(user, response);
        } catch (JwtException e) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "Refresh token invalid or expired");
        }
    }

    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh_token", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    @Transactional
    public void verifyEmail(String token) {
        // In a real system, look up a verification token table
        // For MVP: decode the token as a signed JWT containing userId
        try {
            Claims claims = jwtConfig.parseToken(token);
            UUID userId = UUID.fromString(claims.getSubject());
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "INVALID_TOKEN", "Invalid token"));
            user.setVerified(true);
            userRepository.save(user);
        } catch (JwtException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_TOKEN", "Invalid or expired token");
        }
    }

    public void forgotPassword(String email) {
        // Always return 204 to prevent enumeration — just log/send email in real impl
        userRepository.findByEmail(email).ifPresent(user -> {
            // In production: generate a reset token, store it, send email
        });
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        try {
            Claims claims = jwtConfig.parseToken(token);
            UUID userId = UUID.fromString(claims.getSubject());
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "INVALID_TOKEN", "Invalid token"));
            user.setPasswordHash(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } catch (JwtException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INVALID_TOKEN", "Invalid or expired token");
        }
    }

    private AuthResponse buildAuthResponse(User user, HttpServletResponse response) {
        String accessToken = jwtConfig.generateAccessToken(user.getUserId(), user.getEmail());
        String refreshToken = jwtConfig.generateRefreshToken(user.getUserId());

        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtConfig.getExpirySeconds() * 48)); // 30 days approx
        response.addCookie(cookie);

        return new AuthResponse(accessToken, "Bearer", jwtConfig.getExpirySeconds(), mapper.toUserProfile(user));
    }

    private String extractRefreshCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> "refresh_token".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst().orElse(null);
    }
}
