package com.menghor.smart_shop.feature.customer.service.impl;

import com.menghor.smart_shop.constants.ErrorMessages;
import com.menghor.smart_shop.exceptions.error.NotFoundException;
import com.menghor.smart_shop.feature.auth.models.UserEntity;
import com.menghor.smart_shop.feature.auth.repository.UserRepository;
import com.menghor.smart_shop.feature.customer.dto.request.ShopRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.ShopResponseDto;
import com.menghor.smart_shop.feature.customer.mapper.ShopMapper;
import com.menghor.smart_shop.feature.customer.models.ShopEntity;
import com.menghor.smart_shop.feature.customer.repository.ShopRepository;
import com.menghor.smart_shop.feature.customer.service.ShopService;
import com.menghor.smart_shop.utils.database.CustomPaginationResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final ShopMapper shopMapper;
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    @Override
    public CustomPaginationResponseDto<ShopResponseDto> getAllShop(int pageNo, int pageSize, String search) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<ShopEntity> shopPage;
        if (search != null && !search.isEmpty()) {
            log.info("User is get and search by : {}", search);
            shopPage = shopRepository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            shopPage = shopRepository.findAll(pageable);
        }
        List<ShopResponseDto> content = shopPage.getContent().stream().map(shopMapper::toDto).toList();
        log.info("Shop found: {}", content);
        return shopMapper.mapToListDto(content, shopPage);
    }

    @Transactional
    @Override
    public ShopResponseDto createShopForUser(Long userId, ShopRequestDto shopRequestDto) {

        log.info("User id is : {}", userId);
        log.info("Shop request is : {}", shopRequestDto);

        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException(String.format(ErrorMessages.USER_NOT_FOUND, userId)));
        // Ensure user does not already have a shop
        if (user.getShop() != null) {
            log.error("User already manages a shop");
            throw new RuntimeException(ErrorMessages.USER_ALREADY_MANAGES_SHOP);
        }

        ShopEntity shop = shopMapper.toEntity(shopRequestDto);
        shop.setUser(user);
        user.setShop(shop);

        shop = shopRepository.save(shop);
        return shopMapper.toDto(shop);
    }

    @Override
    public ShopResponseDto getShopById(Long id) {
        ShopEntity shopEntity = shopRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.SHOP_NOT_FOUND, id)));
        return shopMapper.toDto(shopEntity);
    }

    @Override
    public ShopResponseDto updateShop(Long id, ShopRequestDto shopRequest) {

        ShopEntity shopEntity = shopRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(ErrorMessages.SHOP_NOT_FOUND, id)));

        // Use MapStruct to update the entity (ignores null fields)
        shopMapper.updateShopFromDto(shopRequest, shopEntity);

        // Save updated shop entity
        ShopEntity updatedShop = shopRepository.save(shopEntity);
        // Convert to DTO and return
        return shopMapper.toDto(updatedShop);
    }
}
