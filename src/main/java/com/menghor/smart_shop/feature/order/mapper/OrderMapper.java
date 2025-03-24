package com.menghor.smart_shop.feature.order.mapper;

import com.menghor.smart_shop.enumations.OrderStatus;
import com.menghor.smart_shop.feature.order.dto.request.OrderRequestDto;
import com.menghor.smart_shop.feature.order.dto.response.CartItemResponseDto;
import com.menghor.smart_shop.feature.order.dto.response.OrderItemResponseDto;
import com.menghor.smart_shop.feature.order.dto.response.OrderResponseDto;
import com.menghor.smart_shop.feature.order.model.CartItemEntity;
import com.menghor.smart_shop.feature.order.model.OrderEntity;
import com.menghor.smart_shop.feature.order.model.OrderItemEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(source = "shop.id", target = "shopId")
    @Mapping(source = "deliveryCharge", target = "deliveryCharge")
    OrderResponseDto toDto(OrderEntity orderEntity);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "size.id", target = "sizeId")
    @Mapping(source = "size.size", target = "sizeName")
    OrderItemResponseDto toDto(OrderItemEntity orderItemEntity);

    @Mapping(source = "shopId", target = "shop.id")
    OrderEntity toEntity(OrderRequestDto orderRequest);

    @AfterMapping
    default void calculateTotalPrice(@MappingTarget OrderItemResponseDto orderItemResponseDto, OrderItemEntity orderItemEntity) {
        orderItemResponseDto.setTotalPrice(orderItemEntity.getPrice() * orderItemEntity.getQuantity());
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateOrderFromDto(OrderStatus status, @MappingTarget OrderEntity entity);
}
