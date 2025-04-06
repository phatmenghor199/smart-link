package com.menghor.smart_shop.feature.customer.dto.request;

import com.menghor.smart_shop.enumations.StatusData;
import com.menghor.smart_shop.feature.setting.dto.request.ImageRequestDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BannerRequestDto {
    private String description;
    private StatusData status;
    private ImageRequestDto image; // Directly embed the image request
}
