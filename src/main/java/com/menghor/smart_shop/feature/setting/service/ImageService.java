package com.menghor.smart_shop.feature.setting.service;

import com.menghor.smart_shop.feature.setting.dto.request.ImageRequestDto;
import com.menghor.smart_shop.feature.setting.dto.resposne.ImageResponseDto;

import java.util.UUID;

public interface ImageService {
    ImageResponseDto storeImage(ImageRequestDto imageRequestDto);

    byte[] getImage(UUID id);

    ImageResponseDto deleteImage(UUID id);

    ImageResponseDto updateImage(UUID id, ImageRequestDto imageUpdateDto);
}