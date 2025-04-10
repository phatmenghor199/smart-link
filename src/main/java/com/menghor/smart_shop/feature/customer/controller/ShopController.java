package com.menghor.smart_shop.feature.customer.controller;

import com.menghor.smart_shop.constants.SuccessMessages;
import com.menghor.smart_shop.exceptions.response.ApiResponse;
import com.menghor.smart_shop.feature.customer.dto.request.ShopRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.ShopResponseDto;
import com.menghor.smart_shop.feature.customer.service.ShopService;
import com.menghor.smart_shop.utils.database.CustomPaginationResponseDto;
import com.menghor.smart_shop.utils.pagiantion.PaginationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shop")
@RequiredArgsConstructor
public class ShopController {
    private final ShopService shopService;

    @GetMapping
    public ApiResponse<CustomPaginationResponseDto<ShopResponseDto>> getAllShop(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(value = "search", required = false) String search) {

        PaginationUtils.validatePagination(pageNo, pageSize);

        CustomPaginationResponseDto<ShopResponseDto> shopResponse = shopService.getAllShop(pageNo - 1, pageSize, search);
        return new ApiResponse<>(SuccessMessages.SUCCESS, SuccessMessages.ALL_DATA_FETCHED_SUCCESSFULLY, shopResponse);
    }

    // Endpoint to create a shop for the user
    @PostMapping("/{userId}/buy-service")
    public ApiResponse<ShopResponseDto> createShop(@PathVariable Long userId, @RequestBody ShopRequestDto shopRequestDto) {
        final ShopResponseDto shopForUser = shopService.createShopForUser(userId, shopRequestDto);
        return new ApiResponse<>(SuccessMessages.SUCCESS, SuccessMessages.ALL_DATA_FETCHED_SUCCESSFULLY, shopForUser);
    }

    @GetMapping("/{id}")
    public ApiResponse<ShopResponseDto> getShopDetail(@PathVariable Long id) {
        final ShopResponseDto shopById = shopService.getShopById(id);
        return new ApiResponse<>(SuccessMessages.SUCCESS, SuccessMessages.ALL_DATA_FETCHED_SUCCESSFULLY, shopById);
    }

    @PutMapping("/{id}")
    public ApiResponse<ShopResponseDto> updateShop(@PathVariable Long id, @RequestBody ShopRequestDto shop) {
        final ShopResponseDto updateShop = shopService.updateShop(id, shop);
        return new ApiResponse<>(SuccessMessages.SUCCESS, SuccessMessages.ALL_DATA_FETCHED_SUCCESSFULLY, updateShop);
    }
}
