package com.menghor.smart_shop.feature.customer.mapper;

import com.menghor.smart_shop.feature.customer.dto.request.BannerRequestDto;
import com.menghor.smart_shop.feature.customer.dto.request.BannerUpdateRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.BannerResponseDto;
import com.menghor.smart_shop.feature.customer.models.BannerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BannerMapper {
    BannerMapper INSTANCE = Mappers.getMapper(BannerMapper.class);

    BannerEntity toEntity(BannerRequestDto bannerRequestDto);
    BannerEntity toEntity(BannerUpdateRequestDto bannerUpdateRequestDto);

    @Mapping(source = "shop.id", target = "shopId") // Map shop ID to banner DTO
    BannerResponseDto toDto(BannerEntity bannerEntity);

    List<BannerResponseDto> toDtoList(List<BannerEntity> bannerEntities);

}
