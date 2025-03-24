package com.menghor.smart_shop.feature.setting.service.impl;

import com.menghor.smart_shop.exceptoins.error.NotFoundException;
import com.menghor.smart_shop.feature.setting.dto.request.ImageRequestDto;
import com.menghor.smart_shop.feature.setting.dto.resposne.ImageResponseDto;
import com.menghor.smart_shop.feature.setting.mapper.ImageMapper;
import com.menghor.smart_shop.feature.setting.model.ImageEntity;
import com.menghor.smart_shop.feature.setting.repository.ImageRepository;
import com.menghor.smart_shop.feature.setting.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;

    @Override
    public ImageResponseDto storeImage(ImageRequestDto imageRequestDto) {
        log.info("Storing image with name: {}", imageRequestDto.getImageType());

        ImageEntity imageEntity = imageMapper.toEntity(imageRequestDto);
        imageEntity = imageRepository.save(imageEntity);
        log.info("Image stored with ID: {}", imageEntity.getId());
        return imageMapper.toDto(imageEntity);
    }

    @Override
    public byte[] getImage(UUID id) {
        ImageEntity imageEntity = imageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Image not found"));

        return Base64.getDecoder().decode(imageEntity.getBase64Image());
    }

    @Override
    public ImageResponseDto deleteImage(UUID id) {
        log.info("Deleting image with ID: {}", id);

        ImageEntity image = imageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Image not found"));

        imageRepository.delete(image);
        log.info("Image deleted with ID: {}", id);

        return imageMapper.toDto(image);
    }

    @Override
    public ImageResponseDto updateImage(UUID id, ImageRequestDto imageUpdateDto) {
        log.info("Updating image with ID: {}", id);

        ImageEntity imageEntity = imageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Image not found"));

        if (imageUpdateDto.getBase64Image() != null && !imageUpdateDto.getBase64Image().isEmpty()) {
            imageEntity.setBase64Image(imageUpdateDto.getBase64Image());
        }

        if (imageUpdateDto.getImageType() != null && !imageUpdateDto.getImageType().isEmpty()) {
            imageEntity.setImageType(imageUpdateDto.getImageType());
        }

        imageEntity = imageRepository.save(imageEntity);

        log.info("Image updated with ID: {}", imageEntity.getId());

        return imageMapper.toDto(imageEntity);
    }
}