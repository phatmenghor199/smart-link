package com.menghor.smart_shop.feature.customer.service;

import com.menghor.smart_shop.feature.customer.dto.request.ShopRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.ShopResponseDto;
import com.menghor.smart_shop.utils.database.CustomPaginationResponseDto;

public interface ShopService {

    CustomPaginationResponseDto<ShopResponseDto> getAllShop(int pageNo, int pageSize, String search);

    ShopResponseDto createShopForUser(Long userId, ShopRequestDto shop);

    ShopResponseDto getShopById(Long id);

    ShopResponseDto updateShop(Long id, ShopRequestDto shop);
}
