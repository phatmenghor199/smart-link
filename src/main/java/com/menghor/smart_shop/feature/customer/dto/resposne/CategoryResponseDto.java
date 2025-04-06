package com.menghor.smart_shop.feature.customer.dto.resposne;

import com.menghor.smart_shop.enumations.StatusData;
import com.menghor.smart_shop.feature.setting.dto.resposne.ImageResponseDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryResponseDto {
    private Long id;
    private String name;
    private Long shopId;
    private StatusData status;
    private ImageResponseDto image; // Include image data directly
    private LocalDateTime createdAt;

}
