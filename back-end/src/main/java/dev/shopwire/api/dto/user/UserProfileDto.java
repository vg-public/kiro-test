package dev.shopwire.api.dto.user;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserProfileDto(
        UUID user_id,
        String email,
        String first_name,
        String last_name,
        String phone,
        boolean is_verified,
        OffsetDateTime created_at
) {}
