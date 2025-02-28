package com.menghor.smart_shop.feature.customer.mapper;

import com.menghor.smart_shop.feature.customer.dto.request.BannerRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.BannerResponseDto;
import com.menghor.smart_shop.feature.customer.models.BannerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BannerMapper {
    BannerMapper INSTANCE = Mappers.getMapper(BannerMapper.class);

    BannerEntity toEntity(BannerRequestDto bannerRequestDto);

    @Mapping(source = "shop.id", target = "shopId") // Map shop ID to banner DTO
    BannerResponseDto toDto(BannerEntity bannerEntity);


    @Mapping(target = "id", ignore = true) // Prevent ID modification
    void updateBannerFromDto(BannerRequestDto dto, @MappingTarget BannerEntity entity);
}
