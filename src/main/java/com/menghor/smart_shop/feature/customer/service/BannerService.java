package com.menghor.smart_shop.feature.customer.service;

import com.menghor.smart_shop.feature.customer.dto.request.BannerRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.BannerResponseDto;
import com.menghor.smart_shop.feature.customer.models.BannerEntity;

public interface BannerService {
    BannerResponseDto createBanner( BannerRequestDto bannerDto);
}
