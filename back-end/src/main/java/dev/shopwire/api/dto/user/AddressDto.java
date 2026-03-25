package dev.shopwire.api.dto.user;

import java.util.UUID;

public record AddressDto(
        UUID address_id,
        String label,
        String full_name,
        String line1,
        String line2,
        String city,
        String state,
        String postal_code,
        String country,
        boolean is_default
) {}
