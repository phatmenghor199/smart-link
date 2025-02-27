package com.menghor.smart_shop.feature.customer.dto.request;

import lombok.Data;

@Data
public class BannerUpdateRequestDto {
    private Long id;
    private String description;
    private String imageUrl;
}