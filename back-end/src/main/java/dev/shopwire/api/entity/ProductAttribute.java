package dev.shopwire.api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "SW_PRODUCT_ATTRIBUTE")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProductAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attribute_id")
    private Integer attributeId;

    @Column(nullable = false, unique = true, length = 100)
    private String name;
}
