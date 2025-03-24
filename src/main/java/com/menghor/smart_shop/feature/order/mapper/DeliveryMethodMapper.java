package com.menghor.smart_shop.feature.order.mapper;

import com.menghor.smart_shop.feature.order.dto.request.DeliveryMethodRequestDto;
import com.menghor.smart_shop.feature.order.dto.response.DeliveryMethodResponseDto;
import com.menghor.smart_shop.feature.order.model.DeliveryMethodEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DeliveryMethodMapper {

    DeliveryMethodMapper INSTANCE = Mappers.getMapper(DeliveryMethodMapper.class);

    DeliveryMethodEntity toEntity(DeliveryMethodRequestDto dto);
    
    DeliveryMethodResponseDto toDto(DeliveryMethodEntity entity);
}