package dev.shopwire.api.controller;

import dev.shopwire.api.dto.search.SearchResponse;
import dev.shopwire.api.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<SearchResponse> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String category,
            @RequestParam(name = "price_min", required = false) BigDecimal priceMin,
            @RequestParam(name = "price_max", required = false) BigDecimal priceMax,
            @RequestParam(defaultValue = "relevance") String sort) {
        return ResponseEntity.ok(searchService.search(q, page, limit, category, priceMin, priceMax, sort));
    }

    @GetMapping("/suggestions")
    public ResponseEntity<Map<String, List<String>>> suggestions(@RequestParam String q) {
        return ResponseEntity.ok(searchService.suggestions(q));
    }
}
