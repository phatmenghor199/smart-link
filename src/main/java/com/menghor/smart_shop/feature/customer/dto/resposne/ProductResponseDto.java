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
    private String size;
    private Double price;
    private StatusData status;

    private Double finalPrice;
    private String promotionStatus;

    private Long categoryId;
    private Long shopId;

    private DiscountType discountType;
    private Double discountValue;
    private LocalDate discountStartDate;
    private LocalDate discountEndDate;

    // Add image response field
    private ImageResponseDto image;

    private List<ProductSizeResponseDto> sizes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}