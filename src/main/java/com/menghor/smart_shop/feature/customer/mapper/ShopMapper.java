package com.menghor.smart_shop.feature.customer.mapper;

import com.menghor.smart_shop.feature.customer.dto.request.ShopRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.ShopResponseDto;
import com.menghor.smart_shop.feature.customer.models.ShopEntity;
import com.menghor.smart_shop.utils.database.CustomPaginationResponseDto;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ShopMapper {
    ShopMapper INSTANCE = Mappers.getMapper(ShopMapper.class);

    ShopEntity toEntity(ShopRequestDto dto);
    ShopResponseDto toDto(ShopEntity entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateShopFromDto(ShopRequestDto dto, @MappingTarget ShopEntity entity);

    default CustomPaginationResponseDto<ShopResponseDto> mapToListDto(List<ShopResponseDto> content, Page<ShopEntity> shopEntities) {
        CustomPaginationResponseDto<ShopResponseDto> shopResponse = new CustomPaginationResponseDto<>();
        shopResponse.setContent(content);
        shopResponse.setPageNo(shopEntities.getNumber() + 1); // Convert 0-indexed to 1-indexed page number
        shopResponse.setPageSize(shopEntities.getSize());
        shopResponse.setTotalElements(shopEntities.getTotalElements());
        shopResponse.setTotalPages(shopEntities.getTotalPages());
        shopResponse.setLast(shopEntities.isLast());
        return shopResponse;
    }
}
