package com.menghor.smart_shop.feature.customer.dto.resposne;

import com.menghor.smart_shop.enumations.StatusData;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BannerResponseDto {
    private Long id;
    private String description;
    private String imageUrl;
    private Long shopId;
    private StatusData status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
