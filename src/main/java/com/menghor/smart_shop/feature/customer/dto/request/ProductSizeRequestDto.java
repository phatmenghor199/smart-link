package com.menghor.smart_shop.feature.customer.dto.request;

import com.menghor.smart_shop.enumations.DiscountType;
import com.menghor.smart_shop.enumations.StatusData;
import com.menghor.smart_shop.feature.setting.dto.request.ImageRequestDto;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ProductSizeRequestDto {
    private Long id;
    private String size;
    private Double price;

    // Discount fields
    private DiscountType discountType;
    private Double discountValue;
    private LocalDate discountStartDate;
    private LocalDate discountEndDate;

    // Main image for the product size
    private ImageRequestDto image;

    // Additional images for the product size
    private List<ImageRequestDto> additionalImages;

    private StatusData status = StatusData.ACTIVE;
}