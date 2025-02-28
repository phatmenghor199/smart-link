package com.menghor.smart_shop.feature.customer.service;

import com.menghor.smart_shop.feature.customer.dto.request.BannerRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.BannerResponseDto;

import java.util.List;

public interface BannerService {
    BannerResponseDto createBanner(BannerRequestDto bannerDto);
    List<BannerResponseDto> getBannersByShop();
    BannerResponseDto updateBanner(Long bannerId, BannerRequestDto updateRequest);
    BannerResponseDto deleteBanner(Long bannerId);

    List<BannerResponseDto> getBannersByShopId(Long shopId);
}
