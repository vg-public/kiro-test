package dev.shopwire.api.dto.user;

import jakarta.validation.constraints.NotBlank;

public record AddressRequest(
        String label,
        @NotBlank String full_name,
        @NotBlank String line1,
        String line2,
        @NotBlank String city,
        @NotBlank String state,
        @NotBlank String postal_code,
        String country,
        Boolean is_default
) {}
