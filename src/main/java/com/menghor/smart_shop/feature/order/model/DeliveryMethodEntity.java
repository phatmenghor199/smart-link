package com.menghor.smart_shop.feature.order.model;

import com.menghor.smart_shop.feature.customer.models.ShopEntity;
import com.menghor.smart_shop.utils.database.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "delivery_methods")
@Data
public class DeliveryMethodEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // e.g., "SELF", "G&T DELIVERY"
    private Double price; // Delivery price

    @ManyToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private ShopEntity shop; // Each delivery method belongs to one shop
}