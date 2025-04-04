package com.menghor.smart_shop.feature.customer.mapper;

import com.menghor.smart_shop.feature.customer.dto.request.BannerRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.BannerResponseDto;
import com.menghor.smart_shop.feature.customer.models.BannerEntity;
import org.hibernate.sql.Update;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BannerMapper {
    BannerMapper INSTANCE = Mappers.getMapper(BannerMapper.class);

    BannerEntity toEntity(BannerRequestDto bannerRequestDto);

    @Mapping(source = "shop.id", target = "shopId") // Map shop ID to banner DTO
    BannerResponseDto toDto(BannerEntity bannerEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateBannerFromDto(BannerRequestDto dto, @MappingTarget BannerEntity entity);
}
