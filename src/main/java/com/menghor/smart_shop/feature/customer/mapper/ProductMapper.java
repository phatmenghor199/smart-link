package com.menghor.smart_shop.feature.customer.mapper;

import com.menghor.smart_shop.feature.customer.dto.request.ProductRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.ProductResponseDto;
import com.menghor.smart_shop.feature.customer.models.ProductEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "shop.id", target = "shopId")
    @Mapping(target = "finalPrice", expression = "java(product.getFinalPrice())") // Mapping finalPrice
    @Mapping(target = "promotionStatus", expression = "java(product.getPromotionStatus().name())") // Mapping promotionStatus
    ProductResponseDto toDto(ProductEntity product);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "shop", ignore = true)
    @Mapping(target = "discountType", source = "discountType") // Ensure this is mapped
    @Mapping(target = "discountValue", source = "discountValue") // Ensure this is mapped
    @Mapping(target = "discountStartDate", source = "discountStartDate") // Ensure this is mapped
    @Mapping(target = "discountEndDate", source = "discountEndDate") // Ensure this is mapped
    ProductEntity toEntity(ProductRequestDto productRequestDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProductFromDto(ProductRequestDto dto, @MappingTarget ProductEntity entity);
}
