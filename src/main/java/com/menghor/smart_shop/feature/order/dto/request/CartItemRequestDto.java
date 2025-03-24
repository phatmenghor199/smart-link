package com.menghor.smart_shop.feature.order.dto.request;

import lombok.Data;

@Data
public class CartItemRequestDto {
    private Long productId;
    private Long sizeId;
    private Integer quantity;
}