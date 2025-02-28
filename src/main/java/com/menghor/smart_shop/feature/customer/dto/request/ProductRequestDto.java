package com.menghor.smart_shop.feature.customer.dto.request;

import com.menghor.smart_shop.enumations.DiscountType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProductRequestDto {
    private String name;
    private Double price;
    private String description;
    private Long categoryId;

    // Discount fields
    private DiscountType discountType; // Discount type (PERCENTAGE or FIXED_AMOUNT)
    private Double discountValue; // Discount value (e.g., 10% or $5)
    private LocalDate discountStartDate; // Discount start date
    private LocalDate discountEndDate; // Discount end date
}