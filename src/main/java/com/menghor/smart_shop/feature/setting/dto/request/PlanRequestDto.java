package com.menghor.smart_shop.feature.setting.dto.request;

import com.menghor.smart_shop.enumations.Status;
import lombok.Data;

@Data
public class PlanRequestDto {
    private String name;
    private String description;
    private Integer durationDays;
    private Status status;
    private Double price;
    private Integer maxProducts;
    private Boolean allowBanners;
    private Boolean allowPromotions;
    private Boolean allowDelivery;
}
