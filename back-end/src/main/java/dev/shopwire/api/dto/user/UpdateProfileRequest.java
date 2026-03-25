package dev.shopwire.api.dto.user;

public record UpdateProfileRequest(
        String first_name,
        String last_name,
        String phone
) {}
