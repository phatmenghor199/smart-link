package com.menghor.smart_shop.feature.customer.mapper;

import com.menghor.smart_shop.feature.customer.dto.request.ShopInformationRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.ShopInformationResponseDto;
import com.menghor.smart_shop.feature.customer.models.ShopInformationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ShopInformationMapper {
    ShopInformationMapper INSTANCE = Mappers.getMapper(ShopInformationMapper.class);

    ShopInformationEntity toEntity(ShopInformationRequestDto dto);

    ShopInformationResponseDto toDto(ShopInformationEntity entity);

    void updateFromDto(ShopInformationRequestDto dto, @MappingTarget ShopInformationEntity entity);
}