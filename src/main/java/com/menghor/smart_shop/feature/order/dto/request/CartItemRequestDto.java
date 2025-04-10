package com.menghor.smart_shop.feature.order.dto.request;

import lombok.Data;

@Data
public class CartItemRequestDto {
    private Long productId;     // Required: ID of the product to add
    private Long sizeId;        // Optional: If product has sizes, specify which size
    private Integer quantity = 1; // Default to 1 if not specified
}