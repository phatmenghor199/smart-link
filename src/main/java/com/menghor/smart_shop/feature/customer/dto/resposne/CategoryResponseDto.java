package com.menghor.smart_shop.feature.customer.dto.resposne;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryResponseDto {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}