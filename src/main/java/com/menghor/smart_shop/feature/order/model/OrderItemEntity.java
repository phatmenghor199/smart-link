package com.menghor.smart_shop.feature.order.model;

import com.menghor.smart_shop.feature.customer.models.ProductEntity;
import com.menghor.smart_shop.utils.database.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "order_items")
@Data
public class OrderItemEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order; // Each order item belongs to one order

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product; // Each order item is linked to a product

    private Integer quantity; // Quantity of the product in the order
    private Double price; // Price of the product when the order was placed

}
