package com.menghor.smart_shop.feature.customer.service;

import com.menghor.smart_shop.feature.customer.dto.request.ShopInformationRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.ShopInformationResponseDto;

public interface ShopInformationService {
    ShopInformationResponseDto createOrUpdateShopInformation(ShopInformationRequestDto shopRequestDto);

    ShopInformationResponseDto getShopInformation(Long id);

    ShopInformationResponseDto deleteShopInformation(Long id);
}