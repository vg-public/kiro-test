package dev.shopwire.api.dto.auth;

import dev.shopwire.api.dto.user.UserProfileDto;

public record AuthResponse(
        String access_token,
        String token_type,
        long expires_in,
        UserProfileDto user
) {}
