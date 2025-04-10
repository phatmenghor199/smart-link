package com.menghor.smart_shop.feature.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponseDto {
    // Cart identification
    private Long id;
    private Long shopId;
    private String shopName;

    // Cart contents
    private List<CartItemResponseDto> cartItems = new ArrayList<>();
    private Integer itemCount = 0;       // Number of unique items
    private Integer totalQuantity = 0;   // Total quantity of all items

    // Cart financials
    private Double subtotal = 0.0;       // Sum of (originalPrice * quantity) for all items
    private Double totalDiscount = 0.0;  // Sum of all discounts applied
    private Double total = 0.0;          // Final amount (subtotal - totalDiscount)

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String formattedTimestamp; // Human-readable timestamp
}