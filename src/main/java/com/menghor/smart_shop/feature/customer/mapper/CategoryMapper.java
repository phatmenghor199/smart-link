package com.menghor.smart_shop.feature.customer.mapper;

import com.menghor.smart_shop.feature.customer.dto.request.CategoryRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.CategoryResponseDto;
import com.menghor.smart_shop.feature.customer.models.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    CategoryEntity toEntity(CategoryRequestDto categoryRequestDto);
    CategoryResponseDto toDto(CategoryEntity categoryEntity);

    @Mapping(target = "id", ignore = true) // Prevent ID modification
    void updateCategoryFromDto(CategoryRequestDto dto, @MappingTarget CategoryEntity entity);

}
