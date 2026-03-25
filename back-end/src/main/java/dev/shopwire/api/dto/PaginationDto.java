package dev.shopwire.api.dto;

public record PaginationDto(int page, int limit, long total, int pages) {}
