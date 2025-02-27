package com.menghor.smart_shop.feature.customer.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BannerRequestDto {

    @NotBlank(message = "Description cannot be empty")
    private String description;

    @NotBlank(message = "Image URL cannot be empty")
    private String imageUrl;
}
