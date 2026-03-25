package dev.shopwire.api.service;

import dev.shopwire.api.dto.catalog.*;
import dev.shopwire.api.dto.PaginationDto;
import dev.shopwire.api.dto.search.SearchResponse;
import dev.shopwire.api.entity.*;
import dev.shopwire.api.exception.ApiException;
import dev.shopwire.api.repository.*;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final DtoMapper mapper;

    public List<CategoryDto> getCategories() {
        return categoryRepository.findByParentIsNullAndActiveTrue().stream()
                .map(mapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    public SearchResponse listProducts(int page, int limit, String category, String brand,
                                       BigDecimal priceMin, BigDecimal priceMax,
                                       Integer rating, Boolean prime, Boolean inStock,
                                       String badge, String sort) {
        Pageable pageable = buildPageable(page, limit, sort);
        Specification<Product> spec = buildProductSpec(category, brand, priceMin, priceMax, rating, prime, inStock, badge);
        Page<Product> productPage = productRepository.findAll(spec, pageable);
        return buildSearchResponse(null, page, limit, productPage);
    }

    public ProductDetailDto getProduct(UUID productId) {
        Product product = productRepository.findByProductIdAndActiveTrue(productId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Product not found"));
        return mapper.toProductDetail(product);
    }

    public ReviewsResponse getReviews(UUID productId, int page, int limit, String sort) {
        if (!productRepository.existsById(productId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Product not found");
        }
        Sort reviewSort = switch (sort) {
            case "recent" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "rating_asc" -> Sort.by(Sort.Direction.ASC, "rating");
            case "rating_desc" -> Sort.by(Sort.Direction.DESC, "rating");
            default -> Sort.by(Sort.Direction.DESC, "helpfulCount");
        };
        Pageable pageable = PageRequest.of(page - 1, limit, reviewSort);
        Page<Review> reviewPage = reviewRepository.findByProductProductId(productId, pageable);

        Map<Integer, Long> breakdown = new LinkedHashMap<>();
        for (int i = 5; i >= 1; i--) {
            breakdown.put(i, reviewRepository.countByProductIdAndRating(productId, (short) i));
        }

        List<ReviewDto> reviews = reviewPage.getContent().stream()
                .map(mapper::toReviewDto)
                .collect(Collectors.toList());

        PaginationDto pagination = new PaginationDto(page, limit, reviewPage.getTotalElements(),
                reviewPage.getTotalPages());
        return new ReviewsResponse(pagination, breakdown, reviews);
    }

    @Transactional
    public ReviewDto submitReview(UUID productId, UUID userId, ReviewRequest req) {
        Product product = productRepository.findByProductIdAndActiveTrue(productId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Product not found"));

        if (reviewRepository.existsByProductProductIdAndUserUserId(productId, userId)) {
            throw new ApiException(HttpStatus.CONFLICT, "REVIEW_EXISTS", "Review already submitted");
        }

        boolean hasPurchase = orderRepository.existsByUserUserIdAndItemsVariantVariantId(
                userId, product.getVariants().stream().findFirst().map(ProductVariant::getVariantId).orElse(null));

        dev.shopwire.api.entity.User user = new dev.shopwire.api.entity.User();
        user.setUserId(userId);

        Review review = Review.builder()
                .product(product)
                .user(user)
                .rating((short) req.rating().intValue())
                .title(req.title())
                .body(req.body())
                .verified(hasPurchase)
                .build();
        review = reviewRepository.save(review);

        // Update product avg_rating and review_count
        updateProductRating(product);

        return mapper.toReviewDto(reviewRepository.findById(review.getReviewId()).orElse(review));
    }

    private void updateProductRating(Product product) {
        Page<Review> all = reviewRepository.findByProductProductId(product.getProductId(),
                PageRequest.of(0, Integer.MAX_VALUE));
        double avg = all.getContent().stream().mapToInt(r -> r.getRating()).average().orElse(0);
        product.setAvgRating(BigDecimal.valueOf(avg).setScale(2, java.math.RoundingMode.HALF_UP));
        product.setReviewCount((int) all.getTotalElements());
        productRepository.save(product);
    }

    private Pageable buildPageable(int page, int limit, String sort) {
        Sort s = switch (sort != null ? sort : "featured") {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "basePrice");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "basePrice");
            case "rating" -> Sort.by(Sort.Direction.DESC, "avgRating");
            case "newest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            default -> Sort.by(Sort.Direction.DESC, "featured").and(Sort.by(Sort.Direction.DESC, "avgRating"));
        };
        return PageRequest.of(page - 1, limit, s);
    }

    private Specification<Product> buildProductSpec(String category, String brand,
                                                     BigDecimal priceMin, BigDecimal priceMax,
                                                     Integer rating, Boolean prime, Boolean inStock, String badge) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isTrue(root.get("active")));
            if (category != null) predicates.add(cb.equal(root.get("category").get("slug"), category));
            if (brand != null) predicates.add(cb.equal(root.get("brand").get("slug"), brand));
            if (priceMin != null) predicates.add(cb.greaterThanOrEqualTo(root.get("basePrice"), priceMin));
            if (priceMax != null) predicates.add(cb.lessThanOrEqualTo(root.get("basePrice"), priceMax));
            if (rating != null) predicates.add(cb.greaterThanOrEqualTo(root.get("avgRating"), BigDecimal.valueOf(rating)));
            if (prime != null) predicates.add(cb.equal(root.get("prime"), prime));
            if (badge != null) predicates.add(cb.equal(root.get("badge"), badge));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public SearchResponse buildSearchResponse(String query, int page, int limit, Page<Product> productPage) {
        List<ProductSummaryDto> results = productPage.getContent().stream()
                .map(mapper::toProductSummary)
                .collect(Collectors.toList());

        PaginationDto pagination = new PaginationDto(page, limit,
                productPage.getTotalElements(), productPage.getTotalPages());

        // Build facets from results
        Map<String, Long> catCounts = results.stream()
                .collect(Collectors.groupingBy(ProductSummaryDto::category, Collectors.counting()));
        Map<String, Long> brandCounts = results.stream()
                .filter(p -> p.brand() != null)
                .collect(Collectors.groupingBy(ProductSummaryDto::brand, Collectors.counting()));

        List<SearchResponse.FacetItem> catFacets = catCounts.entrySet().stream()
                .map(e -> new SearchResponse.FacetItem(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        List<SearchResponse.FacetItem> brandFacets = brandCounts.entrySet().stream()
                .map(e -> new SearchResponse.FacetItem(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        BigDecimal minPrice = results.stream().map(ProductSummaryDto::base_price)
                .filter(Objects::nonNull).min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
        BigDecimal maxPrice = results.stream().map(ProductSummaryDto::base_price)
                .filter(Objects::nonNull).max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);

        SearchResponse.Facets facets = new SearchResponse.Facets(catFacets, brandFacets,
                new SearchResponse.PriceRange(minPrice, maxPrice));

        return new SearchResponse(query, pagination, results, facets);
    }
}
