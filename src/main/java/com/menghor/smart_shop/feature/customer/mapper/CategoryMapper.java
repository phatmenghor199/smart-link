package com.menghor.smart_shop.feature.customer.mapper;

import com.menghor.smart_shop.feature.customer.dto.request.CategoryRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.CategoryResponseDto;
import com.menghor.smart_shop.feature.customer.models.CategoryEntity;
import com.menghor.smart_shop.feature.setting.mapper.ImageMapper;
import com.menghor.smart_shop.utils.database.CustomPaginationResponseDto;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ImageMapper.class})
public abstract class CategoryMapper {

    @Autowired
    protected ImageMapper imageMapper;

    @Mapping(target = "image", source = "image")
    public abstract CategoryEntity toEntity(CategoryRequestDto categoryRequestDto);

    @Mapping(source = "shop.id", target = "shopId")
    @Mapping(source = "image", target = "image")
    public abstract CategoryResponseDto toDto(CategoryEntity categoryEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "image.referenceType", ignore = true) // Explicitly ignore referenceType during update
    public abstract void updateCategoryFromDto(CategoryRequestDto dto, @MappingTarget CategoryEntity entity);

    public CustomPaginationResponseDto<CategoryResponseDto> toPaginationDto(
            List<CategoryResponseDto> content,
            Page<CategoryEntity> page) {
        CustomPaginationResponseDto<CategoryResponseDto> response = new CustomPaginationResponseDto<>();
        response.setContent(content);
        response.setPageNo(page.getNumber() + 1);
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setLast(page.isLast());
        return response;
    }
}