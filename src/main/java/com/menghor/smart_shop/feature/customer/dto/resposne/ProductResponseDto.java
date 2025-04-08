package com.menghor.smart_shop.feature.customer.dto.resposne;

import com.menghor.smart_shop.enumations.DiscountType;
import com.menghor.smart_shop.enumations.StatusData;
import com.menghor.smart_shop.feature.setting.dto.resposne.ImageResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {
    private Long id;
    private String name;
    private Double price;
    private String description;
    private StatusData status;
    private Double finalPrice;
    private String promotionStatus;

    private Long categoryId;
    private Long shopId;

    // Promotion details
    private DiscountType discountType;
    private Double discountValue;
    private LocalDate discountStartDate;
    private LocalDate discountEndDate;

    // Main image for the product
    private ImageResponseDto mainImage;

    // Additional images for the product
    private List<ImageResponseDto> additionalImages;

    // Product sizes (optional)
    private List<ProductSizeResponseDto> sizes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}