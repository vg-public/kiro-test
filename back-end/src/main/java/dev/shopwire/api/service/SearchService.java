package dev.shopwire.api.service;

import dev.shopwire.api.dto.search.SearchResponse;
import dev.shopwire.api.entity.Product;
import dev.shopwire.api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ProductRepository productRepository;
    private final CatalogService catalogService;

    public SearchResponse search(String q, int page, int limit, String category,
                                 BigDecimal priceMin, BigDecimal priceMax, String sort) {
        Sort s = "price_asc".equals(sort) ? Sort.by(Sort.Direction.ASC, "basePrice")
                : "price_desc".equals(sort) ? Sort.by(Sort.Direction.DESC, "basePrice")
                : "rating".equals(sort) ? Sort.by(Sort.Direction.DESC, "avgRating")
                : "newest".equals(sort) ? Sort.by(Sort.Direction.DESC, "createdAt")
                : Sort.by(Sort.Direction.DESC, "avgRating"); // relevance fallback

        Pageable pageable = PageRequest.of(page - 1, limit, s);
        Page<Product> productPage = productRepository.fullTextSearch(q, pageable);
        return catalogService.buildSearchResponse(q, page, limit, productPage);
    }

    public Map<String, List<String>> suggestions(String q) {
        Pageable pageable = PageRequest.of(0, 10);
        List<String> suggestions = productRepository.findTitleSuggestions(q, pageable);
        return Map.of("suggestions", suggestions);
    }
}
