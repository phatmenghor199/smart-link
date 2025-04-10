package com.menghor.smart_shop.feature.order.mapper;

import com.menghor.smart_shop.enumations.DiscountType;
import com.menghor.smart_shop.feature.order.dto.response.CartItemResponseDto;
import com.menghor.smart_shop.feature.order.model.CartItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    CartItemMapper INSTANCE = Mappers.getMapper(CartItemMapper.class);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.description", target = "productDescription")
    @Mapping(source = "size.id", target = "sizeId")
    @Mapping(source = "size.size", target = "sizeName")
    @Mapping(target = "productImageUrl", source = "product.mainImage", qualifiedByName = "mapImageUrl")
    @Mapping(target = "sizeImageUrl", source = "size.mainImage", qualifiedByName = "mapImageUrl")
    @Mapping(target = "additionalImageUrls", source = "product.additionalImages", qualifiedByName = "mapAdditionalImageUrls")
    @Mapping(target = "categoryId", source = "product.category.id")
    @Mapping(target = "categoryName", source = "product.category.name")
    @Mapping(target = "originalPrice", source = ".", qualifiedByName = "calculateOriginalPrice")
    @Mapping(target = "unitPrice", source = ".", qualifiedByName = "calculateUnitPrice")
    @Mapping(target = "totalPrice", source = ".", qualifiedByName = "calculateTotalPrice")
    @Mapping(target = "discountAmount", source = ".", qualifiedByName = "calculateDiscountAmount")
    @Mapping(target = "discountDisplay", source = ".", qualifiedByName = "formatDiscountDisplay")
    CartItemResponseDto toDto(CartItemEntity item);

    @Named("mapImageUrl")
    default String mapImageUrl(com.menghor.smart_shop.feature.setting.model.ImageEntity image) {
        return image != null && image.getId() != null
            ? "/api/v1/images/" + image.getId()
            : null;
    }

    @Named("mapAdditionalImageUrls")
    default java.util.List<String> mapAdditionalImageUrls(java.util.List<com.menghor.smart_shop.feature.setting.model.ImageEntity> images) {
        return images != null
            ? images.stream()
                .map(img -> "/api/v1/images/" + img.getId())
                .collect(java.util.stream.Collectors.toList())
            : null;
    }

    @Named("calculateOriginalPrice")
    default Double calculateOriginalPrice(CartItemEntity item) {
        if (item.getSize() != null && item.getSize().getPrice() != null) {
            return item.getSize().getPrice();
        }
        return item.getProduct() != null && item.getProduct().getPrice() != null
            ? item.getProduct().getPrice()
            : 0.0;
    }

    @Named("calculateUnitPrice")
    default Double calculateUnitPrice(CartItemEntity item) {
        return item.getPrice() != null
            ? item.getPrice()
            : calculateOriginalPrice(item);
    }

    @Named("calculateTotalPrice")
    default Double calculateTotalPrice(CartItemEntity item) {
        return calculateUnitPrice(item) * (item.getQuantity() != null ? item.getQuantity() : 0);
    }

    @Named("calculateDiscountAmount")
    default Double calculateDiscountAmount(CartItemEntity item) {
        Double originalPrice = calculateOriginalPrice(item);
        Double unitPrice = calculateUnitPrice(item);
        return (originalPrice - unitPrice) * (item.getQuantity() != null ? item.getQuantity() : 0);
    }

    @Named("formatDiscountDisplay")
    default String formatDiscountDisplay(CartItemEntity item) {
        Double originalPrice = calculateOriginalPrice(item);
        Double unitPrice = calculateUnitPrice(item);

        if (item.getDiscountType() != null && item.getDiscountValue() != null) {
            if (item.getDiscountType() == DiscountType.PERCENTAGE) {
                return item.getDiscountValue().intValue() + "% OFF";
            } else {
                return "$" + item.getDiscountValue() + " OFF";
            }
        } else if (originalPrice > unitPrice) {
            int percentage = (int) Math.round(((originalPrice - unitPrice) / originalPrice) * 100);
            return percentage > 0 ? percentage + "% OFF" : null;
        }
        return null;
    }
}