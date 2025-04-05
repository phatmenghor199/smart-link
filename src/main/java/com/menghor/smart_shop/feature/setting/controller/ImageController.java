package com.menghor.smart_shop.feature.setting.controller;

import com.menghor.smart_shop.exceptoins.error.NotFoundException;
import com.menghor.smart_shop.exceptoins.response.ApiResponse;
import com.menghor.smart_shop.feature.setting.dto.request.ImageRequestDto;
import com.menghor.smart_shop.feature.setting.dto.resposne.ImageResponseDto;
import com.menghor.smart_shop.feature.setting.model.ImageEntity;
import com.menghor.smart_shop.feature.setting.repository.ImageRepository;
import com.menghor.smart_shop.feature.setting.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;
    private final ImageRepository imageRepository;

    @PostMapping
    public ApiResponse<ImageResponseDto> storeImage(@RequestBody ImageRequestDto imageRequestDto) {
        ImageResponseDto imageResponse = imageService.storeImage(imageRequestDto);
        return new ApiResponse<>("Success", "Images uploaded successfully", imageResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable UUID id) {
        try {
            byte[] imageData = imageService.getImage(id);

            HttpHeaders headers = new HttpHeaders();
            ImageEntity imageEntity = imageRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Image not found"));
            if (imageEntity.getImageType().equalsIgnoreCase("png")) {
                headers.setContentType(MediaType.IMAGE_PNG);
            } else if (imageEntity.getImageType().equalsIgnoreCase("jpg") || imageEntity.getImageType().equalsIgnoreCase("jpeg")) {
                headers.setContentType(MediaType.IMAGE_JPEG);
            } else {
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            }
            headers.setContentLength(imageData.length);

            return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(e.getMessage().getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(e.getMessage().getBytes());
        }
    }
}