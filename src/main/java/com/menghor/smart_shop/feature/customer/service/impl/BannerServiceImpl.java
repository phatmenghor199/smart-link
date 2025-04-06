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
import com.menghor.smart_shop.feature.setting.model.ImageEntity;
import com.menghor.smart_shop.utils.database.CustomPaginationResponseDto;
import com.menghor.smart_shop.utils.database.SecurityUtils;
import com.menghor.smart_shop.utils.pagiantion.PaginationUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
    @Transactional
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

        // If image data is provided in the request, set reference type
        if (bannerEntity.getImage() != null) {
            bannerEntity.getImage().setReferenceType("banner");
            log.info("Setting referenceType to 'banner' for new image");
        }

        BannerEntity savedBanner = bannerRepository.save(bannerEntity);
        log.info("Banner created successfully with ID: {}", savedBanner.getId());

        // Log the saved image details for debugging
        if (savedBanner.getImage() != null) {
            log.info("Saved image with ID: {}, referenceType: {}",
                    savedBanner.getImage().getId(),
                    savedBanner.getImage().getReferenceType());
        }

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

        // Log current image state for debugging
        if (banner.getImage() != null) {
            log.info("Before update: Image ID: {}, referenceType: {}",
                    banner.getImage().getId(),
                    banner.getImage().getReferenceType());
        } else {
            log.info("Before update: Banner has no image");
        }

        // Handle image update
        if (updateRequest.getImage() != null) {
            if (banner.getImage() == null) {
                // Create new image if none exists
                ImageEntity newImage = new ImageEntity();
                newImage.setImageType(updateRequest.getImage().getImageType());
                newImage.setBase64Image(updateRequest.getImage().getBase64Image());
                newImage.setReferenceType("banner");
                banner.setImage(newImage);
                log.info("Created new image with referenceType: banner");
            } else {
                // Update existing image instead of replacing it
                banner.getImage().setImageType(updateRequest.getImage().getImageType());
                banner.getImage().setBase64Image(updateRequest.getImage().getBase64Image());
                banner.getImage().setReferenceType("banner"); // Explicitly set referenceType again
                log.info("Updated existing image and set referenceType: banner");
            }
        }

        // Update other fields
        bannerMapper.updateBannerFromDto(updateRequest, banner);

        // IMPORTANT FIX: Re-set the referenceType AFTER the mapper update
        // This ensures the mapper doesn't overwrite our referenceType
        if (banner.getImage() != null) {
            banner.getImage().setReferenceType("banner");
            log.info("Re-applying referenceType 'banner' after mapper update");
        }

        // Update status if specifically provided in the request
        if (updateRequest.getStatus() != null) {
            banner.setStatus(updateRequest.getStatus());
        }

        BannerEntity updatedBanner = bannerRepository.save(banner);
        log.info("Banner updated successfully with ID: {}", updatedBanner.getId());

        // Additional verification log after save
        if (updatedBanner.getImage() != null) {
            log.info("Verified after save: Image ID: {}, referenceType: {}",
                    updatedBanner.getImage().getId(),
                    updatedBanner.getImage().getReferenceType());
        }

        return bannerMapper.toDto(updatedBanner);
    }

    @Override
    public BannerResponseDto getBannerById(Long bannerId) {
        BannerEntity banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new NotFoundException("Banner not found with id: " + bannerId));
        return bannerMapper.toDto(banner);
    }

    @Override
    @Transactional
    public BannerResponseDto deleteBanner(Long bannerId) {
        Long userId = securityUtils.getUserIdFromToken();

        BannerEntity banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.BANNER_NOT_FOUND, bannerId)));

        getUserOwnedShop(userId, banner.getShop().getId());
        log.info("Deleting banner ID: {} for shop ID: {}", bannerId, banner.getShop().getId());

        // The associated image will be automatically deleted due to orphanRemoval=true in the relationship
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

        // Use the sorted repository method to ensure consistent ordering
        List<BannerEntity> banners = bannerRepository.findByShopIdOrderByCreatedAtDesc(shopId);

        return banners.stream()
                .map(bannerMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CustomPaginationResponseDto<BannerResponseDto> getBannersByShop(BannerFilterDto filterDto) {
        Long shopId = securityUtils.getShopIdFromToken();
        log.info("Filtering banners for shop ID: {} with criteria: {}", shopId, filterDto);

        // Validate pagination parameters
        PaginationUtils.validatePagination(
                filterDto.getPageNo() != null ? filterDto.getPageNo() : 1,
                filterDto.getPageSize() != null ? filterDto.getPageSize() : 10);

        // Create specification using filter criteria
        Specification<BannerEntity> spec = BannerSpecification.createSpecification(
                filterDto.getSearch(),
                filterDto.getStatus(),
                shopId
        );

        // Create pageable object with sorting by ID (or creation date) in descending order
        Pageable pageable = PageRequest.of(
                filterDto.getPageNo() - 1,
                filterDto.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        // Execute the query with specification, pagination, and sorting
        Page<BannerEntity> bannerPage = bannerRepository.findAll(spec, pageable);

        // Map entities to DTOs
        List<BannerResponseDto> bannerDtos = bannerPage.getContent().stream()
                .map(bannerMapper::toDto)
                .collect(Collectors.toList());

        // Create pagination response
        return bannerMapper.toPaginationDto(bannerDtos, bannerPage);
    }

    @Override
    @Transactional
    public BannerResponseDto toggleBannerStatus(Long bannerId) {
        Long userId = securityUtils.getUserIdFromToken();

        BannerEntity banner = bannerRepository.findById(bannerId)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.BANNER_NOT_FOUND, bannerId)));

        getUserOwnedShop(userId, banner.getShop().getId());

        // Toggle status
        if (banner.getStatus() == StatusData.ACTIVE) {
            banner.setStatus(StatusData.INACTIVE);
        } else {
            banner.setStatus(StatusData.ACTIVE);
        }

        bannerRepository.save(banner);
        log.info("Banner status updated to {} for ID: {}", banner.getStatus(), bannerId);

        return bannerMapper.toDto(banner);
    }

    private ShopEntity getUserOwnedShop(Long userId, Long shopId) {
        return shopRepository.findByIdAndUserId(shopId, userId)
                .orElseThrow(() -> {
                    log.error("User {} does not own shop {}", userId, shopId);
                    return new NotFoundException(String.format(ErrorMessages.USER_DOES_NOT_OWN_SHOP, userId, shopId));
                });
    }
}