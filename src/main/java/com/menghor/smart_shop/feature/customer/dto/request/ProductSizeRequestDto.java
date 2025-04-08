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
public class ProductSizeRequestDto {
    private Long id;
    private String size;
    private Double price;

    // Optional discount fields
    private DiscountType discountType;
    private Double discountValue;
    private LocalDate discountStartDate;
    private LocalDate discountEndDate;

    // Optional images
    private ImageRequestDto image;
    private List<ImageRequestDto> additionalImages;

    private StatusData status = StatusData.ACTIVE;
}