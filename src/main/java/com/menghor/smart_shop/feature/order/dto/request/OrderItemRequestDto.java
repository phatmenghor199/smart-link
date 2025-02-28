package com.menghor.smart_shop.feature.order.dto.request;

import lombok.Data;

@Data
public class OrderItemRequestDto {
    private Integer quantity; // Quantity of the product
    private Long productId; // Product to be ordered
}