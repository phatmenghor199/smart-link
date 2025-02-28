package com.menghor.smart_shop.feature.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDto {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double price;
}