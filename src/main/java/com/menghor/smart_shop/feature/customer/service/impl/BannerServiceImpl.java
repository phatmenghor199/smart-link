package com.menghor.smart_shop.feature.customer.service.impl;

import com.menghor.smart_shop.constants.ErrorMessages;
import com.menghor.smart_shop.exceptoins.error.NotFoundException;
import com.menghor.smart_shop.feature.customer.dto.request.BannerRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.BannerResponseDto;
import com.menghor.smart_shop.feature.customer.mapper.BannerMapper;
import com.menghor.smart_shop.feature.customer.models.BannerEntity;
import com.menghor.smart_shop.feature.customer.models.ShopEntity;
import com.menghor.smart_shop.feature.customer.repository.BannerRepository;
import com.menghor.smart_shop.feature.customer.repository.ShopRepository;
import com.menghor.smart_shop.feature.customer.service.BannerService;
import com.menghor.smart_shop.utils.database.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {
    private final BannerRepository bannerRepository;
    private final ShopRepository shopRepository;
    private final SecurityUtils securityUtils;
    private final BannerMapper bannerMapper;


    @Override
    public BannerResponseDto createBanner(BannerRequestDto createRequest) {

        Long userId = securityUtils.getUserIdFromToken();
        Long shopId = securityUtils.getShopIdFromToken();

        log.info("Creating banner for shopId: {} by userId: {}", shopId, userId);

        ShopEntity shop = getUserOwnedShop(userId, shopId);


        // Map the DTO to BannerEntity
        BannerEntity bannerEntity = bannerMapper.toEntity(createRequest);

        // Set the shop for the banner
        bannerEntity.setShop(shop);

        BannerEntity savedBanner = bannerRepository.save(bannerEntity);
        log.info("Banner created successfully with ID: {}", savedBanner.getId());

        return bannerMapper.toDto(savedBanner);
    }

    @Override
    public List<BannerResponseDto> getBannersByShop() {
        Long userId = securityUtils.getUserIdFromToken();
        Long shopId = securityUtils.getShopIdFromToken();

        log.info("Fetching banners for shopId: {} by userId: {}", shopId, userId);
        getUserOwnedShop(userId, shopId);

        List<BannerEntity> banners = bannerRepository.findByShopId(shopId);

        return banners.stream()
                .map(bannerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public BannerResponseDto updateBanner(Long bannerId, BannerRequestDto updateRequest) {
        Long userId = securityUtils.getUserIdFromToken();

        BannerEntity banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.BANNER_NOT_FOUND, bannerId)));

        log.info("Updating banner ID: {} for shop ID: {}", bannerId, banner.getShop().getId());

        getUserOwnedShop(userId, banner.getShop().getId());

        bannerMapper.updateBannerFromDto(updateRequest, banner);

        BannerEntity updatedBanner = bannerRepository.save(banner);

        log.info("Banner updated successfully with ID: {}", updatedBanner.getId());
        return bannerMapper.toDto(updatedBanner);
    }

    @Override
    public BannerResponseDto deleteBanner(Long bannerId) {
        Long userId = securityUtils.getUserIdFromToken();

        BannerEntity banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.BANNER_NOT_FOUND, bannerId)));

        getUserOwnedShop(userId, banner.getShop().getId());
        log.info("Deleting banner ID: {} for shop ID: {}", bannerId, banner.getShop().getId());
        bannerRepository.delete(banner);

        log.info("Banner deleted successfully: {}", bannerId);
        return bannerMapper.toDto(banner);
    }

    @Override
    public List<BannerResponseDto> getBannersByShopId(Long shopId) {
        // Ensure the shop exists
        ShopEntity shop = shopRepository.findById(shopId)
                .orElseThrow(() -> {
                    log.error("Shop with ID {} not found", shopId);
                    return new NotFoundException(String.format(ErrorMessages.SHOP_NOT_FOUND, shopId));
                });

        // Fetch banners for the shop
        List<BannerEntity> banners = bannerRepository.findByShopId(shopId);
        // Convert to DTO and return
        return banners.stream()
                .map(bannerMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Helper method to validate if the user owns the shop.
     *
     * @param userId The user ID
     * @param shopId The shop ID
     * @return ShopEntity if the user owns the shop
     */
    private ShopEntity getUserOwnedShop(Long userId, Long shopId) {
        return shopRepository.findByIdAndUserId(shopId, userId)
                .orElseThrow(() -> {
                    log.error("User {} does not own shop {}", userId, shopId);
                    return new NotFoundException(String.format(ErrorMessages.USER_DOES_NOT_OWN_SHOP, userId, shopId));
                });
    }

}

