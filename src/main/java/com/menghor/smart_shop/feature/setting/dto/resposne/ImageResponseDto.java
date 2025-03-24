package com.menghor.smart_shop.feature.setting.dto.resposne;

import lombok.Data;

import java.util.UUID;

@Data
public class ImageResponseDto {
    private UUID id;
    private String url;
}