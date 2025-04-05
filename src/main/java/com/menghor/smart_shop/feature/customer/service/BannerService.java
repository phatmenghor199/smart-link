package com.menghor.smart_shop.feature.customer.service;

import com.menghor.smart_shop.feature.customer.dto.request.BannerFilterDto;
import com.menghor.smart_shop.feature.customer.dto.request.BannerRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.BannerResponseDto;
import com.menghor.smart_shop.utils.database.CustomPaginationResponseDto;

import java.util.List;

public interface BannerService {
    BannerResponseDto createBanner(BannerRequestDto bannerDto);
    BannerResponseDto updateBanner(Long bannerId, BannerRequestDto updateRequest);
    BannerResponseDto getBannerById(Long bannerId);
    BannerResponseDto deleteBanner(Long bannerId);
    List<BannerResponseDto> getBannersByShopId(Long shopId);
    CustomPaginationResponseDto<BannerResponseDto> getBannersByShop(BannerFilterDto filterDto);
}
