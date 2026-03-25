package dev.shopwire.api.service;

import dev.shopwire.api.dto.catalog.*;
import dev.shopwire.api.dto.cart.CartDto;
import dev.shopwire.api.dto.cart.CartItemDto;
import dev.shopwire.api.dto.order.*;
import dev.shopwire.api.dto.user.AddressDto;
import dev.shopwire.api.dto.user.UserProfileDto;
import dev.shopwire.api.dto.wishlist.WishlistItemDto;
import dev.shopwire.api.entity.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DtoMapper {

    public UserProfileDto toUserProfile(User user) {
        return new UserProfileDto(
                user.getUserId(), user.getEmail(),
                user.getFirstName(), user.getLastName(),
                user.getPhone(), user.isVerified(), user.getCreatedAt()
        );
    }

    public AddressDto toAddressDto(UserAddress a) {
        return new AddressDto(
                a.getAddressId(), a.getLabel(), a.getFullName(),
                a.getLine1(), a.getLine2(), a.getCity(), a.getState(),
                a.getPostalCode(), a.getCountry(), a.isDefault()
        );
    }

    public AddressDto fromAddressSnapshot(Map<String, Object> snap) {
        if (snap == null) return null;
        return new AddressDto(
                snap.get("address_id") != null ? java.util.UUID.fromString(snap.get("address_id").toString()) : null,
                (String) snap.get("label"),
                (String) snap.get("full_name"),
                (String) snap.get("line1"),
                (String) snap.get("line2"),
                (String) snap.get("city"),
                (String) snap.get("state"),
                (String) snap.get("postal_code"),
                (String) snap.get("country"),
                Boolean.TRUE.equals(snap.get("is_default"))
        );
    }

    public CategoryDto toCategoryDto(Category c) {
        List<CategoryDto> children = c.getChildren().stream()
                .filter(Category::isActive)
                .map(this::toCategoryDto)
                .collect(Collectors.toList());
        return new CategoryDto(
                c.getCategoryId(),
                c.getParent() != null ? c.getParent().getCategoryId() : null,
                c.getName(), c.getSlug(), c.getImageUrl(), children
        );
    }

    public ProductSummaryDto toProductSummary(Product p) {
        String primaryImage = p.getImages().stream()
                .filter(ProductImage::isPrimary)
                .map(ProductImage::getUrl)
                .findFirst()
                .orElse(p.getImages().isEmpty() ? null : p.getImages().get(0).getUrl());

        boolean inStock = p.getVariants().stream()
                .anyMatch(v -> v.isActive() && v.getStockQty() > 0);

        Integer discountPct = null;
        if (p.getSalePrice() != null && p.getBasePrice().compareTo(BigDecimal.ZERO) > 0) {
            discountPct = p.getBasePrice().subtract(p.getSalePrice())
                    .multiply(BigDecimal.valueOf(100))
                    .divide(p.getBasePrice(), 0, RoundingMode.HALF_UP)
                    .intValue();
        }

        return new ProductSummaryDto(
                p.getProductId(), p.getTitle(), p.getSlug(),
                p.getBrand() != null ? p.getBrand().getName() : null,
                p.getCategory().getName(),
                primaryImage, p.getBasePrice(), p.getSalePrice(),
                p.getCurrency(), discountPct, p.getBadge(),
                p.getAvgRating(), p.getReviewCount(), p.isPrime(), inStock
        );
    }

    public ProductDetailDto toProductDetail(Product p) {
        ProductSummaryDto summary = toProductSummary(p);
        List<ProductDetailDto.ImageDto> images = p.getImages().stream()
                .map(i -> new ProductDetailDto.ImageDto(i.getUrl(), i.getAltText(), i.isPrimary()))
                .collect(Collectors.toList());
        List<VariantDto> variants = p.getVariants().stream()
                .filter(ProductVariant::isActive)
                .map(this::toVariantDto)
                .collect(Collectors.toList());
        List<String> bullets = p.getBulletPoints() != null ? List.of(p.getBulletPoints()) : List.of();

        return new ProductDetailDto(
                summary.product_id(), summary.title(), summary.slug(),
                summary.brand(), summary.category(), summary.primary_image(),
                summary.base_price(), summary.sale_price(), summary.currency(),
                summary.discount_pct(), summary.badge(), summary.avg_rating(),
                summary.review_count(), summary.is_prime(), summary.in_stock(),
                p.getDescription(), bullets, images, variants
        );
    }

    public VariantDto toVariantDto(ProductVariant v) {
        List<VariantDto.AttributeDto> attrs = v.getAttributeValues().stream()
                .map(av -> new VariantDto.AttributeDto(av.getAttribute().getName(), av.getValue()))
                .collect(Collectors.toList());
        return new VariantDto(v.getVariantId(), v.getSku(), v.getTitle(),
                v.getPrice(), v.getStockQty(), v.isActive(), attrs);
    }

    public ReviewDto toReviewDto(Review r) {
        String userName = r.getUser().getFirstName() + " " + r.getUser().getLastName().charAt(0) + ".";
        return new ReviewDto(r.getReviewId(), userName, r.getRating(),
                r.getTitle(), r.getBody(), r.isVerified(), r.getHelpfulCount(), r.getCreatedAt());
    }

    public CartDto toCartDto(Cart cart) {
        List<CartItemDto> items = cart.getItems().stream()
                .map(this::toCartItemDto)
                .collect(Collectors.toList());
        BigDecimal subtotal = items.stream()
                .map(CartItemDto::line_total)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int itemCount = items.stream().mapToInt(CartItemDto::quantity).sum();
        return new CartDto(cart.getCartId(), items, subtotal, itemCount);
    }

    public CartItemDto toCartItemDto(CartItem ci) {
        ProductVariant v = ci.getVariant();
        Product p = v.getProduct();
        String imageUrl = p.getImages().stream()
                .filter(ProductImage::isPrimary)
                .map(ProductImage::getUrl)
                .findFirst().orElse(null);
        BigDecimal lineTotal = v.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity()));
        return new CartItemDto(
                ci.getCartItemId(), v.getVariantId(), p.getProductId(),
                p.getTitle(), v.getTitle(), imageUrl,
                v.getPrice(), ci.getQuantity(), lineTotal, p.isPrime()
        );
    }

    public OrderSummaryDto toOrderSummary(Order o) {
        int itemCount = o.getItems().stream().mapToInt(OrderItem::getQuantity).sum();
        return new OrderSummaryDto(o.getOrderId(), o.getStatus().name(),
                o.getTotalAmount(), o.getCurrency(), itemCount, o.getCreatedAt());
    }

    public OrderDetailDto toOrderDetail(Order o) {
        OrderSummaryDto summary = toOrderSummary(o);
        AddressDto addr = fromAddressSnapshot(o.getShippingAddress());
        List<OrderItemDto> items = o.getItems().stream().map(this::toOrderItemDto).collect(Collectors.toList());
        List<ShipmentDto> shipments = o.getShipments().stream().map(this::toShipmentDto).collect(Collectors.toList());
        return new OrderDetailDto(
                summary.order_id(), summary.status(), summary.total_amount(),
                summary.currency(), summary.item_count(), summary.created_at(),
                addr, o.getSubtotal(), o.getShippingCost(), o.getTaxAmount(),
                o.getDiscountAmount(), o.getNotes(), items, shipments
        );
    }

    public OrderItemDto toOrderItemDto(OrderItem oi) {
        return new OrderItemDto(oi.getOrderItemId(), oi.getProductTitle(),
                oi.getVariantTitle(), oi.getUnitPrice(), oi.getQuantity(), oi.getLineTotal());
    }

    public ShipmentDto toShipmentDto(Shipment s) {
        return new ShipmentDto(s.getShipmentId(), s.getCarrier(), s.getTrackingNumber(),
                s.getShippedAt(), s.getEstimatedAt(), s.getDeliveredAt());
    }

    public WishlistItemDto toWishlistItemDto(Wishlist w) {
        return new WishlistItemDto(w.getWishlistId(), toProductSummary(w.getVariant().getProduct()),
                w.getVariant().getVariantId(), w.getAddedAt());
    }
}
