package com.menghor.smart_shop.feature.customer.dto.request;

import com.menghor.smart_shop.enumations.DiscountType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProductSizeRequestDto {
    private Long id;
    private String size;
    private Double price;
    private DiscountType discountType;
    private Double discountValue;
    private LocalDate discountStartDate;
    private LocalDate discountEndDate;
}