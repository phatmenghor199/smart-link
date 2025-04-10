package com.menghor.smart_shop.feature.order.model;

import com.menghor.smart_shop.enumations.DiscountType;
import com.menghor.smart_shop.feature.customer.models.ProductEntity;
import com.menghor.smart_shop.feature.customer.models.ProductSizeEntity;
import com.menghor.smart_shop.utils.database.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cart_items")
@Data
public class CartItemEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private CartEntity cart;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "size_id")
    private ProductSizeEntity size;

    private Integer quantity;
    private Double price; // Unit price (with discounts already applied)

    @Enumerated(EnumType.STRING)
    private DiscountType discountType; // Only stored for information, not for calculation

    private Double discountValue; // Only stored for information, not for calculation
}