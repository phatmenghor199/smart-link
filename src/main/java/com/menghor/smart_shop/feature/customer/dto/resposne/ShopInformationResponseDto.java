package com.menghor.smart_shop.feature.customer.dto.resposne;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShopInformationResponseDto {
    private Long id;
    private String name;
    private String address;
    private String phoneNumber;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}