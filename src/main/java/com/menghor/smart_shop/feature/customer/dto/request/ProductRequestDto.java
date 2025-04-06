package com.menghor.smart_shop.feature.customer.dto.request;

import com.menghor.smart_shop.enumations.DiscountType;
import com.menghor.smart_shop.enumations.StatusData;
import com.menghor.smart_shop.feature.setting.dto.request.ImageRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {
    private String name;
    private Double price;
    private String description;
    private Long categoryId;
    private StatusData status = StatusData.ACTIVE; // Default to active

    // Discount fields
    private DiscountType discountType; // Discount type (PERCENTAGE or FIXED_AMOUNT)
    private Double discountValue; // Discount value (e.g., 10% or $5)
    private LocalDate discountStartDate; // Discount start date
    private LocalDate discountEndDate; // Discount end date

    private List<ProductSizeRequestDto> sizes;

    // Image field
    private ImageRequestDto image;
}