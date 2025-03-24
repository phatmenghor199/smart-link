package com.menghor.smart_shop.feature.customer.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BannerRequestDto {

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotBlank(message = "Image URL cannot be empty")
    private String imageUrl;
}
