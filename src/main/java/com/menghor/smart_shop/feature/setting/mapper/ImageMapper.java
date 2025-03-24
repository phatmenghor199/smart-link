package com.menghor.smart_shop.feature.setting.mapper;

import com.menghor.smart_shop.feature.setting.dto.request.ImageRequestDto;
import com.menghor.smart_shop.feature.setting.dto.resposne.ImageResponseDto;
import com.menghor.smart_shop.feature.setting.model.ImageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    ImageMapper INSTANCE = Mappers.getMapper(ImageMapper.class);

    ImageEntity toEntity(ImageRequestDto dto);

    @Mapping(target = "url", expression = "java(generateUrl(entity.getId()))")
    ImageResponseDto toDto(ImageEntity entity);

    default String generateUrl(UUID id) {
        return "/images/" + id;
    }
}