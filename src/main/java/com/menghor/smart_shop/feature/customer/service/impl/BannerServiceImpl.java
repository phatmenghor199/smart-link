package com.menghor.smart_shop.feature.customer.service.impl;

import com.menghor.smart_shop.constants.ErrorMessages;
import com.menghor.smart_shop.enumations.StatusData;
import com.menghor.smart_shop.exceptoins.error.NotFoundException;
import com.menghor.smart_shop.feature.customer.dto.request.BannerFilterDto;
import com.menghor.smart_shop.feature.customer.dto.request.BannerRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.BannerResponseDto;
import com.menghor.smart_shop.feature.customer.mapper.BannerMapper;
import com.menghor.smart_shop.feature.customer.models.BannerEntity;
import com.menghor.smart_shop.feature.customer.models.ShopEntity;
import com.menghor.smart_shop.feature.customer.repository.BannerRepository;
import com.menghor.smart_shop.feature.customer.repository.ShopRepository;
import com.menghor.smart_shop.feature.customer.service.BannerService;
import com.menghor.smart_shop.feature.customer.specification.BannerSpecification;
import com.menghor.smart_shop.feature.setting.service.ImageService;
import com.menghor.smart_shop.utils.database.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {
    private final BannerRepository bannerRepository;
    private final ShopRepository shopRepository;
    private final SecurityUtils securityUtils;
    private final BannerMapper bannerMapper;
    private final ImageService imageService;

    @Override
    public BannerResponseDto createBanner(BannerRequestDto createRequest) {

        Long userId = securityUtils.getUserIdFromToken();
        Long shopId = securityUtils.getShopIdFromToken();

        log.info("Creating banner for shopId: {} by userId: {}", shopId, userId);

        ShopEntity shop = getUserOwnedShop(userId, shopId);
        BannerEntity bannerEntity = bannerMapper.toEntity(createRequest);
        bannerEntity.setShop(shop);

        // Set status if provided, otherwise use default (ACTIVE)
        if (createRequest.getStatus() != null) {
            bannerEntity.setStatus(createRequest.getStatus());
        } else {
            bannerEntity.setStatus(StatusData.ACTIVE);
        }

        BannerEntity savedBanner = bannerRepository.save(bannerEntity);
        log.info("Banner created successfully with ID: {}", savedBanner.getId());

        return bannerMapper.toDto(savedBanner);
    }

    @Override
    @Transactional
    public BannerResponseDto updateBanner(Long bannerId, BannerRequestDto updateRequest) {
        Long userId = securityUtils.getUserIdFromToken();

        BannerEntity banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.BANNER_NOT_FOUND, bannerId)));

        log.info("Updating banner ID: {} for shop ID: {}", bannerId, banner.getShop().getId());

        getUserOwnedShop(userId, banner.getShop().getId());

        // Store original image URL to check for changes
        String originalImageUrl = banner.getImageUrl();

        // Update banner fields but preserve ID, shop, status, and createdAt timestamp
        bannerMapper.updateBannerFromDto(updateRequest, banner);

        // Update status if specifically provided in the request
        if (updateRequest.getStatus() != null) {
            banner.setStatus(updateRequest.getStatus());
        }

        BannerEntity updatedBanner = bannerRepository.save(banner);

        // If image URL has changed, delete the old image
        if (originalImageUrl != null && !originalImageUrl.equals(updatedBanner.getImageUrl())) {
            deleteImageIfExists(originalImageUrl);
        }

        log.info("Banner updated successfully with ID: {}", updatedBanner.getId());
        return bannerMapper.toDto(updatedBanner);
    }

    @Override
    @Transactional
    public BannerResponseDto deleteBanner(Long bannerId) {
        Long userId = securityUtils.getUserIdFromToken();

        BannerEntity banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.BANNER_NOT_FOUND, bannerId)));

        getUserOwnedShop(userId, banner.getShop().getId());
        log.info("Deleting banner ID: {} for shop ID: {}", bannerId, banner.getShop().getId());

        bannerRepository.delete(banner);

        deleteImageIfExists(banner.getImageUrl());

        log.info("Banner and its associated image deleted successfully: {}", bannerId);
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

        List<BannerEntity> banners = bannerRepository.findByShopId(shopId);

        return banners.stream()
                .map(bannerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BannerResponseDto> getBannersByShop(BannerFilterDto filterDto) {
        Long shopId = securityUtils.getShopIdFromToken();
        log.info("Filtering banners for shop ID: {} with criteria: {}", shopId, filterDto);

        // Create specification using filter criteria
        Specification<BannerEntity> spec = BannerSpecification.createSpecification(
                filterDto.getSearch(),
                filterDto.getStatus(),
                shopId
        );

        // Execute the query with specification
        List<BannerEntity> banners = bannerRepository.findAll(spec);

        // Map to DTOs
        return banners.stream()
                .map(bannerMapper::toDto)
                .collect(Collectors.toList());
    }

    private ShopEntity getUserOwnedShop(Long userId, Long shopId) {
        return shopRepository.findByIdAndUserId(shopId, userId)
                .orElseThrow(() -> {
                    log.error("User {} does not own shop {}", userId, shopId);
                    return new NotFoundException(String.format(ErrorMessages.USER_DOES_NOT_OWN_SHOP, userId, shopId));
                });
    }

    private UUID extractImageIdFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }

        Pattern pattern = Pattern.compile("/images/([a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12})");
        Matcher matcher = pattern.matcher(imageUrl);

        if (matcher.find()) {
            String uuidStr = matcher.group(1);
            try {
                return UUID.fromString(uuidStr);
            } catch (IllegalArgumentException e) {
                log.error("Failed to parse UUID from image URL: {}", imageUrl, e);
                return null;
            }
        }

        log.warn("No UUID found in image URL: {}", imageUrl);
        return null;
    }

    /**
     * Helper method to delete image if URL is valid and image exists
     */
    private void deleteImageIfExists(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            UUID imageId = extractImageIdFromUrl(imageUrl);
            if (imageId != null) {
                try {
                    log.info("Deleting image with ID: {}", imageId);
                    imageService.deleteImage(imageId);
                } catch (NotFoundException e) {
                    // Image was already deleted or not found - just log it
                    log.warn("Image with ID {} not found for deletion", imageId);
                } catch (Exception e) {
                    // Log other errors but don't fail the transaction
                    log.error("Error deleting image with ID {}: {}", imageId, e.getMessage());
                }
            }
        }
    }
}

