package com.menghor.smart_shop.feature.customer.controller;


import com.menghor.smart_shop.exceptoins.response.ApiResponse;
import com.menghor.smart_shop.feature.customer.dto.request.BannerFilterDto;
import com.menghor.smart_shop.feature.customer.dto.request.BannerRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.BannerResponseDto;
import com.menghor.smart_shop.feature.customer.service.BannerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/banner")
@RequiredArgsConstructor
@Slf4j
public class BannerController {
    private final BannerService bannerService;

    /**
     * Create a new banner
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BannerResponseDto> createBanner(@Valid @RequestBody BannerRequestDto createRequest) {
        log.info("Received request to create banner");
        return new ApiResponse<>("Success", "Banner created successfully", bannerService.createBanner(createRequest));
    }

    /**
     * Get all banners for the current shop
     */
    @PostMapping("/shop/all")
    public ApiResponse<List<BannerResponseDto>> getBanners(@RequestBody BannerFilterDto request) {
        log.info("Fetching banners for the current shop");
        final List<BannerResponseDto> bannersByShop = bannerService.getBannersByShop(request);
        return new ApiResponse<>("Success", "Banner by shop successfully", bannersByShop);
    }

    /**
     * Get all banners for a specific shop by ID
     */
    @GetMapping("/shop/{shopId}")
    public ApiResponse<List<BannerResponseDto>> getBannersByShopId(@PathVariable Long shopId) {
        log.info("Fetching banners for shop ID: {}", shopId);
        return new ApiResponse<>("Success", "Banner by shop successfully", bannerService.getBannersByShopId(shopId));
    }


    /**
     * Update a banner by ID
     */
    @PutMapping("/{bannerId}")
    public ApiResponse<BannerResponseDto> updateBanner(
            @PathVariable Long bannerId,
            @Valid @RequestBody BannerRequestDto request
    ) {
        log.info("Updating banner with ID: {}", bannerId);
        final BannerResponseDto bannerResponseDto = bannerService.updateBanner(bannerId, request);
        return new ApiResponse<>("Success", "Banner updated by shop successfully", bannerResponseDto);
    }

    /**
     * Delete a banner by ID
     */
    @DeleteMapping("/{bannerId}")
    public ApiResponse<BannerResponseDto> deleteBanner(@PathVariable Long bannerId) {
        log.info("Deleting banner with ID: {}", bannerId);
        final BannerResponseDto bannerResponseDto = bannerService.deleteBanner(bannerId);
        return new ApiResponse<>("Success", "Banner deleted by shop successfully", bannerResponseDto);
    }
}
