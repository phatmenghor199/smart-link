package com.menghor.smart_shop.feature.customer.dto.request;

import com.menghor.smart_shop.enumations.StatusData;
import lombok.Data;

@Data
public class ProductFilterDto {
    private String search; // For product name search
    private StatusData status; // Filter by product status
    private Long categoryId; // Filter by category
    private Boolean hasPromotion; // Filter products with active promotions
    private Integer pageNo = 1; // Default to page 1
    private Integer pageSize = 10; // Default to 10 items per page
}