package com.menghor.smart_shop.feature.setting.dto.request;

import lombok.Data;

@Data
public class PlanRequestDto {
    private String name;
    private String description;
    private Integer durationDays;
    private Double price;
    private Integer maxProducts;
    private Boolean allowBanners;
    private Boolean allowPromotions;
    private Boolean allowDelivery;
}
