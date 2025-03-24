package com.menghor.smart_shop.feature.order.model;


import com.menghor.smart_shop.enumations.DeliveryCharge;
import com.menghor.smart_shop.enumations.OrderFilterType;
import com.menghor.smart_shop.enumations.OrderStatus;
import com.menghor.smart_shop.feature.customer.models.ShopEntity;
import com.menghor.smart_shop.utils.database.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
public class OrderEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // e.g., "PENDING", "COMPLETED"

    @Enumerated(EnumType.STRING)
    private DeliveryCharge deliveryCharge;

    private String phoneNumber; // Customer phone number
    private String location; // Customer location

    private double deliveryFee; // Delivery fee

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private ShopEntity shop; // Each order belongs to one shop

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<OrderItemEntity> orderItems; // An order can have multiple products

    private Double totalAmount; // Total amount of the order

    @ManyToOne
    @JoinColumn(name = "delivery_method_id")
    private DeliveryMethodEntity deliveryMethod; // Reference to chosen delivery method

    @Enumerated(EnumType.STRING)
    private OrderFilterType orderBy; // e.g., "BY_SHOP", "BY_CUSTOMER"
}
