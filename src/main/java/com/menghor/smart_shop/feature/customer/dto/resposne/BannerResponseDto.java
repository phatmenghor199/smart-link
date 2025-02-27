package com.menghor.smart_shop.feature.customer.dto.resposne;

import lombok.Data;

@Data
public class BannerResponseDto {
    private Long id;
    private String description;
    private String imageUrl;
    private Long shopId;
}
