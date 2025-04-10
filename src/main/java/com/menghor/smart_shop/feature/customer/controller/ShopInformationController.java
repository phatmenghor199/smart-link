package com.menghor.smart_shop.feature.customer.controller;

import com.menghor.smart_shop.exceptions.response.ApiResponse;
import com.menghor.smart_shop.feature.customer.dto.request.ShopInformationRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.ShopInformationResponseDto;
import com.menghor.smart_shop.feature.customer.service.ShopInformationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shop_information")
@RequiredArgsConstructor
@Slf4j
public class ShopInformationController {
    private final ShopInformationService shopService;

    @PostMapping
    public ApiResponse<ShopInformationResponseDto> createOrUpdateShopInformation(@RequestBody ShopInformationRequestDto shopRequestDto) {
        ShopInformationResponseDto createdShop = shopService.createOrUpdateShopInformation(shopRequestDto);
        return new ApiResponse<>("Success", "Shop information created successfully", createdShop);
    }

    @GetMapping("/{id}")
    public ApiResponse<ShopInformationResponseDto> getShopInformation(@PathVariable Long id) {
        ShopInformationResponseDto shopResponseDto = shopService.getShopInformation(id);
        return new ApiResponse<>("Success", "Shop information get by id successfully", shopResponseDto);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<ShopInformationResponseDto> deleteShopInformation(@PathVariable Long id) {
        ShopInformationResponseDto shopInformationResponseDto = shopService.deleteShopInformation(id);
        return new ApiResponse<>("Success", "Shop information deleted successfully", shopInformationResponseDto);
    }
}