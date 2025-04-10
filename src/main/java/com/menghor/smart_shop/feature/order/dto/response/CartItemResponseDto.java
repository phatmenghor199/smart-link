package com.menghor.smart_shop.feature.order.dto.response;

import com.menghor.smart_shop.enumations.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemResponseDto {
    // Cart item details
    private Long id;
    private Integer quantity;

    // Product details
    private Long productId;
    private String productName;
    private String productDescription;
    private Long categoryId;
    private String categoryName;

    // Image URLs instead of full image data
    private String productImageUrl;
    private List<String> additionalImageUrls;

    // Size details (if applicable)
    private Long sizeId;
    private String sizeName;
    private String sizeImageUrl;

    // Pricing details
    private Double originalPrice;  // Original price before discount
    private Double unitPrice;      // Unit price after any discounts
    private Double totalPrice;     // Total price (quantity * unitPrice)

    // Discount details
    private DiscountType discountType;  // Percentage or fixed amount
    private Double discountValue;       // The discount percentage or amount
    private Double discountAmount;      // Total discount amount
    private String discountDisplay;     // Formatted discount text (e.g., "20% OFF")
}