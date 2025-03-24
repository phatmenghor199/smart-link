package com.menghor.smart_shop.feature.order.dto.response;

import com.menghor.smart_shop.feature.customer.dto.resposne.ShopResponseDto;
import com.menghor.smart_shop.feature.customer.models.ShopEntity;
import com.menghor.smart_shop.feature.order.dto.request.OrderItemRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartResponseDto {
    private Long id;
    private List<CartItemResponseDto> cartItems;
    private Long shopId; // Changed from ShopResponseDto to Long shopId
    private Double totalAmount;
    private String createdAt;
    private String updatedAt;
}