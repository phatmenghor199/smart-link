package com.menghor.smart_shop.feature.customer.service;

import com.menghor.smart_shop.feature.customer.dto.request.BannerFilterDto;
import com.menghor.smart_shop.feature.customer.dto.request.BannerRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.BannerResponseDto;
import com.menghor.smart_shop.utils.database.CustomPaginationResponseDto;

import java.util.List;

public interface BannerService {
    // Create a new banner with embedded image
    BannerResponseDto createBanner(BannerRequestDto bannerDto);

    // Update a banner
    BannerResponseDto updateBanner(Long bannerId, BannerRequestDto updateRequest);

    // Get a banner by ID
    BannerResponseDto getBannerById(Long bannerId);

    // Delete a banner
    BannerResponseDto deleteBanner(Long bannerId);

    // Get banners by shop ID
    List<BannerResponseDto> getBannersByShopId(Long shopId);

    // Get banners with pagination and filtering
    CustomPaginationResponseDto<BannerResponseDto> getBannersByShop(BannerFilterDto filterDto);

    // Toggle banner status
    BannerResponseDto toggleBannerStatus(Long bannerId);
}