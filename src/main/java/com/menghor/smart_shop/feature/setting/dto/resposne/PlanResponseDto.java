package com.menghor.smart_shop.feature.setting.dto.resposne;

import com.menghor.smart_shop.enumations.Status;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PlanResponseDto {
    private Long id;
    private String name;
    private String description;
    private Integer durationDays;
    private Double price;
    private Integer maxProducts;
    private Boolean allowBanners;
    private Boolean allowPromotions;
    private Boolean allowDelivery;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}