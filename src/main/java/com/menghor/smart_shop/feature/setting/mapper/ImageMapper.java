package com.menghor.smart_shop.feature.setting.mapper;

import com.menghor.smart_shop.feature.setting.dto.request.ImageRequestDto;
import com.menghor.smart_shop.feature.setting.dto.resposne.ImageResponseDto;
import com.menghor.smart_shop.feature.setting.model.ImageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ImageMapper {

    ImageEntity toEntity(ImageRequestDto dto);

    @Mapping(target = "url", expression = "java(generateUrl(entity.getId()))")
    ImageResponseDto toDto(ImageEntity entity);

    default String generateUrl(UUID id) {
        if (id == null) {
            return null;
        }
        return "/api/v1/images/" + id;
    }
}