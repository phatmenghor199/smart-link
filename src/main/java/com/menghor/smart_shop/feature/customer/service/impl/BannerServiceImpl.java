package com.menghor.smart_shop.feature.customer.service.impl;

import com.menghor.smart_shop.constants.ErrorMessages;
import com.menghor.smart_shop.exceptoins.error.NotFoundException;
import com.menghor.smart_shop.feature.auth.models.UserEntity;
import com.menghor.smart_shop.feature.auth.repository.UserRepository;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {
    private final BannerRepository bannerRepository;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;
    private final BannerMapper bannerMapper;


    @Override
    public BannerResponseDto createBanner(BannerRequestDto createRequest) {

        Long userId = securityUtils.getUserIdFromToken();
        Long shopId = securityUtils.getShopIdFromToken(); // Get shopId from token

        log.info("User id is : {}", userId);
        log.info("Shop id is : {}", shopId);

        if (!userHasAccessToShop(userId, shopId)) {
            throw new NotFoundException(String.format(ErrorMessages.USER_DOES_NOT_OWN_SHOP, userId, shopId));
        }

        // Map the DTO to BannerEntity
        BannerEntity bannerEntity = bannerMapper.toEntity(createRequest);

        // Set the shop for the banner
        bannerEntity.setShop(shopRepository.findById(shopId)
                .orElseThrow(() -> {
                    log.error("Shop with id {} not found", shopId);
                    return new NotFoundException(String.format(ErrorMessages.SHOP_NOT_FOUND, shopId));
                }));


        // Save the banner
        bannerRepository.save(bannerEntity);

        log.info("Banner created successfully");
        return bannerMapper.toDto(bannerEntity);
    }

    private boolean userHasAccessToShop(Long userId, Long shopId) {
        return shopRepository.existsByIdAndUserId(shopId, userId);
    }
}
