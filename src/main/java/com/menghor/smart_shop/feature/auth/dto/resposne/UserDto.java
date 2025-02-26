package com.menghor.smart_shop.feature.auth.dto.resposne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String userRole;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}