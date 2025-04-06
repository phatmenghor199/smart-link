package com.menghor.smart_shop.feature.customer.dto.resposne;

import com.menghor.smart_shop.enumations.DiscountType;
import com.menghor.smart_shop.enumations.StatusData;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProductSizeResponseDto {
    private Long id;
    private String size;
    private Double price;
    private Double finalPrice;
    private String promotionStatus;
    private DiscountType discountType;
    private Double discountValue;
    private LocalDate discountStartDate;
    private LocalDate discountEndDate;
    private StatusData status;
    private Long productId;
}