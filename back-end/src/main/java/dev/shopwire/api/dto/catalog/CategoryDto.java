package dev.shopwire.api.dto.catalog;

import java.util.List;

public record CategoryDto(
        Integer category_id,
        Integer parent_id,
        String name,
        String slug,
        String image_url,
        List<CategoryDto> children
) {}
