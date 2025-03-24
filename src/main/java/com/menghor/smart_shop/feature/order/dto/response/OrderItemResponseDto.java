package com.menghor.smart_shop.feature.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class OrderItemResponseDto {
    private Long id;
    private Long productId;
    private String productName;
    private Long sizeId;
    private String sizeName;
    private Integer quantity;
    private Double price;
    private Double totalPrice; // Added field for total price when quantity is 1
    private String discountType; // Type of promotion (e.g., "DISCOUNT")
    private Double discountValue; // Value of the promotion (e.g., 10% or $5 off)
}