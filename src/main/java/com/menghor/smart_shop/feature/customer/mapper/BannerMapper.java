package com.menghor.smart_shop.feature.customer.mapper;

import com.menghor.smart_shop.feature.customer.dto.request.BannerRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.BannerResponseDto;
import com.menghor.smart_shop.feature.customer.models.BannerEntity;
import com.menghor.smart_shop.feature.setting.mapper.ImageMapper;
import com.menghor.smart_shop.utils.database.CustomPaginationResponseDto;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ImageMapper.class})
public abstract class BannerMapper {

    @Autowired
    protected ImageMapper imageMapper;

    @Mapping(target = "image", source = "image")
    public abstract BannerEntity toEntity(BannerRequestDto bannerRequestDto);

    @Mapping(source = "shop.id", target = "shopId")
    @Mapping(source = "image", target = "image")
    public abstract BannerResponseDto toDto(BannerEntity bannerEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "image.referenceType", ignore = true) // Explicitly ignore referenceType during update
    public abstract void updateBannerFromDto(BannerRequestDto dto, @MappingTarget BannerEntity entity);

    public CustomPaginationResponseDto<BannerResponseDto> toPaginationDto(
            List<BannerResponseDto> content,
            Page<BannerEntity> page) {
        CustomPaginationResponseDto<BannerResponseDto> response = new CustomPaginationResponseDto<>();
        response.setContent(content);
        response.setPageNo(page.getNumber() + 1);
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setLast(page.isLast());
        return response;
    }
}