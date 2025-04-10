package com.menghor.smart_shop.feature.order.mapper;

import com.menghor.smart_shop.feature.order.dto.response.CartResponseDto;
import com.menghor.smart_shop.feature.order.model.CartEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        componentModel = "spring",
        uses = {CartItemMapper.class}
)
public interface CartMapper {
    CartMapper INSTANCE = Mappers.getMapper(CartMapper.class);

    @Mapping(source = "shop.id", target = "shopId")
    @Mapping(source = "shop.name", target = "shopName")
    @Mapping(source = "cartItems", target = "cartItems")
    @Mapping(target = "itemCount", expression = "java(cart != null && cart.getCartItems() != null ? cart.getCartItems().size() : 0)")
    @Mapping(target = "totalQuantity", source = ".", qualifiedByName = "calculateTotalQuantity")
    @Mapping(target = "subtotal", source = ".", qualifiedByName = "calculateSubtotal")
    @Mapping(target = "totalDiscount", source = ".", qualifiedByName = "calculateTotalDiscount")
    @Mapping(target = "total", source = ".", qualifiedByName = "calculateTotal")
    @Mapping(target = "formattedTimestamp", source = "updatedAt", qualifiedByName = "formatTimestamp")
    CartResponseDto toDetailedResponseDto(CartEntity cart);

    @Named("calculateTotalQuantity")
    default Integer calculateTotalQuantity(CartEntity cart) {
        return cart != null && cart.getCartItems() != null
                ? cart.getCartItems().stream()
                .mapToInt(item -> item.getQuantity() != null ? item.getQuantity() : 0)
                .sum()
                : 0;
    }

    @Named("calculateSubtotal")
    default Double calculateSubtotal(CartEntity cart) {
        return cart != null && cart.getCartItems() != null
                ? cart.getCartItems().stream()
                .mapToDouble(item -> {
                    Double price = item.getPrice() != null ? item.getPrice() : 0.0;
                    Integer quantity = item.getQuantity() != null ? item.getQuantity() : 0;
                    return price * quantity;
                })
                .sum()
                : 0.0;
    }

    @Named("calculateTotalDiscount")
    default Double calculateTotalDiscount(CartEntity cart) {
        return cart != null && cart.getCartItems() != null
                ? cart.getCartItems().stream()
                .mapToDouble(item -> {
                    Double originalPrice = getOriginalPrice(item);
                    Double unitPrice = item.getPrice() != null ? item.getPrice() : originalPrice;
                    Integer quantity = item.getQuantity() != null ? item.getQuantity() : 0;
                    return (originalPrice - unitPrice) * quantity;
                })
                .sum()
                : 0.0;
    }

    @Named("calculateTotal")
    default Double calculateTotal(CartEntity cart) {
        return calculateSubtotal(cart) - calculateTotalDiscount(cart);
    }

    @Named("formatTimestamp")
    default String formatTimestamp(java.time.LocalDateTime updatedAt) {
        if (updatedAt == null) return null;
        return "Updated: " + updatedAt.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private Double getOriginalPrice(com.menghor.smart_shop.feature.order.model.CartItemEntity item) {
        if (item.getSize() != null && item.getSize().getPrice() != null) {
            return item.getSize().getPrice();
        }
        return item.getProduct() != null && item.getProduct().getPrice() != null
                ? item.getProduct().getPrice()
                : 0.0;
    }
}