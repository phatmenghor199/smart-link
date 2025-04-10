package com.menghor.smart_shop.feature.customer.service.impl;

import com.menghor.smart_shop.exceptions.error.NotFoundException;
import com.menghor.smart_shop.feature.customer.dto.request.ShopInformationRequestDto;
import com.menghor.smart_shop.feature.customer.dto.resposne.ShopInformationResponseDto;
import com.menghor.smart_shop.feature.customer.mapper.ShopInformationMapper;
import com.menghor.smart_shop.feature.customer.models.ShopInformationEntity;
import com.menghor.smart_shop.feature.customer.repository.ShopInformationRepository;
import com.menghor.smart_shop.feature.customer.service.ShopInformationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopInformationServiceImpl  implements ShopInformationService {

    private final ShopInformationRepository shopRepository;
    private final ShopInformationMapper shopMapper;


    @Override
    public ShopInformationResponseDto createOrUpdateShopInformation(ShopInformationRequestDto shopRequestDto) {
        Optional<ShopInformationEntity> existingShopInfo = shopRepository.findByName(shopRequestDto.getName());
        ShopInformationEntity shopEntity;
        if (existingShopInfo.isPresent()) {
            shopEntity = existingShopInfo.get();
            shopMapper.updateFromDto(shopRequestDto, shopEntity);
        } else {
            shopEntity = shopMapper.toEntity(shopRequestDto);
        }
        shopEntity = shopRepository.save(shopEntity);
        return shopMapper.toDto(shopEntity);
    }

    @Override
    public ShopInformationResponseDto getShopInformation(Long id) {
        ShopInformationEntity shopEntity = shopRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Shop not found"));
        return shopMapper.toDto(shopEntity);
    }

    @Override
    public ShopInformationResponseDto deleteShopInformation(Long id) {
        ShopInformationEntity shopEntity = shopRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Shop not found"));
        shopRepository.delete(shopEntity);
        return shopMapper.toDto(shopEntity);
    }
}
