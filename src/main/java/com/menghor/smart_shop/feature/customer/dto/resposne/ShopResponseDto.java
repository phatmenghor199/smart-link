package com.menghor.smart_shop.feature.customer.dto.resposne;

import com.menghor.smart_shop.feature.auth.dto.resposne.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShopResponseDto {
    private Long id;
    private String name;
    private String location;
    private UserDto user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
