package dev.shopwire.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "SW_VARIANT_ATTRIBUTE_VALUE")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VariantAttributeValue {

    @EmbeddedId
    private VariantAttributeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("variantId")
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("attributeId")
    @JoinColumn(name = "attribute_id")
    private ProductAttribute attribute;

    @Column(nullable = false, length = 100)
    private String value;

    @Embeddable
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
    public static class VariantAttributeId implements Serializable {
        @Column(name = "variant_id")
        private UUID variantId;
        @Column(name = "attribute_id")
        private Integer attributeId;
    }
}
