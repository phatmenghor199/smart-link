package com.menghor.smart_shop.feature.customer.dto.resposne;

import com.menghor.smart_shop.feature.auth.dto.resposne.UserDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ShopResponseDto {
    private Long id;
    private String name;
    private String location;
    private UserDto user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
