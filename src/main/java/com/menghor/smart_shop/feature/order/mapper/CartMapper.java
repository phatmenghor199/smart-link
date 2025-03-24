package com.menghor.smart_shop.feature.order.mapper;

import com.menghor.smart_shop.feature.auth.dto.resposne.UserDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.ShopResponseDto;
import com.menghor.smart_shop.feature.order.dto.request.CartItemRequestDto;
import com.menghor.smart_shop.feature.order.dto.response.CartItemResponseDto;
import com.menghor.smart_shop.feature.order.dto.response.CartResponseDto;
import com.menghor.smart_shop.feature.order.model.CartEntity;
import com.menghor.smart_shop.feature.order.model.CartItemEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CartMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "shop.id", target = "shopId")
    CartResponseDto toCartDto(CartEntity cartEntity); // Renamed method

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName") // Map product name
    @Mapping(source = "size.id", target = "sizeId")
    @Mapping(source = "size.size", target = "sizeName") // Map size name
    @Mapping(source = "price", target = "totalPrice", ignore = true) // Ignore totalPrice, we'll calculate it manually
    CartItemResponseDto toCartItemDto(CartItemEntity cartItemEntity); // Renamed method


    @AfterMapping
    default void calculateTotalPrice(@MappingTarget CartItemResponseDto cartItemResponseDto, CartItemEntity cartItemEntity) {
        cartItemResponseDto.setTotalPrice(cartItemEntity.getPrice() * cartItemEntity.getQuantity());
    }

    @AfterMapping
    default void calculateTotalAmount(@MappingTarget CartResponseDto cartResponseDto, CartEntity cartEntity) {
        double totalAmount = cartEntity.getCartItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        cartResponseDto.setTotalAmount(totalAmount);
    }

    default List<CartItemResponseDto> cartItemsToDto(List<CartItemEntity> cartItems) {
        return cartItems.stream().map(this::toCartItemDto).collect(Collectors.toList());
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCartFromEntity(CartEntity cartEntity, @MappingTarget CartResponseDto cartResponseDto);
}