package com.menghor.smart_shop.feature.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponseDto {
    private Long id;
    private Long productId;
    private String productName; // Added field
    private Long sizeId;
    private String sizeName; // Added field
    private Integer quantity;
    private Double price;
    private Double totalPrice; // Added field for total price when quantity is 1
    private String discountType; // Added field
    private Double discountValue; // Added field
}