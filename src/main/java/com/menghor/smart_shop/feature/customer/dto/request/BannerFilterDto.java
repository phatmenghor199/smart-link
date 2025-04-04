package com.menghor.smart_shop.feature.customer.dto.request;

import com.menghor.smart_shop.enumations.StatusData;
import lombok.Data;

@Data
public class BannerFilterDto {
    private String search;
    private StatusData status;
}