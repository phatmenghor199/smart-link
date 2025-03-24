package com.menghor.smart_shop.feature.setting.dto.request;

import lombok.Data;

@Data
public class ImageRequestDto {
    private String base64Image;
    private String imageType; // png, jpg, etc.
}