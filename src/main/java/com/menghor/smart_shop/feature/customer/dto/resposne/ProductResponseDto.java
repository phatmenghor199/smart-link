package com.menghor.smart_shop.feature.customer.dto.resposne;

import com.menghor.smart_shop.enumations.DiscountType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductResponseDto {
    private Long id;
    private String name;
    private Double price;

    private Double finalPrice;
    private String promotionStatus;

    private Long categoryId;
    private Long shopId;

    private DiscountType discountType;
    private Double discountValue;
    private LocalDate discountStartDate;
    private LocalDate discountEndDate;

    private List<ProductSizeResponseDto> sizes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}