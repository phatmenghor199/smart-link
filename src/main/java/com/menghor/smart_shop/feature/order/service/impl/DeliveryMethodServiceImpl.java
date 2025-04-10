package com.menghor.smart_shop.feature.order.service.impl;

import com.menghor.smart_shop.exceptions.error.NotFoundException;
import com.menghor.smart_shop.feature.customer.models.ShopEntity;
import com.menghor.smart_shop.feature.order.dto.request.DeliveryMethodRequestDto;
import com.menghor.smart_shop.feature.order.dto.response.DeliveryMethodResponseDto;
import com.menghor.smart_shop.feature.order.mapper.DeliveryMethodMapper;
import com.menghor.smart_shop.feature.order.model.DeliveryMethodEntity;
import com.menghor.smart_shop.feature.order.repository.DeliveryMethodRepository;
import com.menghor.smart_shop.feature.order.service.DeliveryMethodService;
import com.menghor.smart_shop.utils.database.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryMethodServiceImpl implements DeliveryMethodService {

    private final DeliveryMethodMapper deliveryMethodMapper;
    private final DeliveryMethodRepository deliveryMethodRepository;
    private final SecurityUtils securityUtils;

    @Override
    public DeliveryMethodResponseDto createDeliveryMethod(DeliveryMethodRequestDto deliveryMethodRequestDto) {
        log.info("Creating delivery method: {}", deliveryMethodRequestDto);
        ShopEntity shopFromToken = securityUtils.getShopFromToken();

        DeliveryMethodEntity deliveryMethodEntity = deliveryMethodMapper.toEntity(deliveryMethodRequestDto);
        deliveryMethodEntity.setShop(shopFromToken);
        deliveryMethodEntity = deliveryMethodRepository.save(deliveryMethodEntity);

        return deliveryMethodMapper.toDto(deliveryMethodEntity);
    }

    @Override
    public DeliveryMethodResponseDto updateDeliveryMethod(Long deliveryMethodId, DeliveryMethodRequestDto deliveryMethodRequestDto) {
        log.info("Updating delivery method with ID: {}", deliveryMethodId);
        DeliveryMethodEntity existingDeliveryMethod = deliveryMethodRepository.findById(deliveryMethodId)
                .orElseThrow(() -> new NotFoundException("Delivery method not found"));

        Long shopId = securityUtils.getShopIdFromToken();
        if (!existingDeliveryMethod.getShop().getId().equals(shopId)) {
            throw new IllegalArgumentException("Delivery method does not belong to the specified shop");
        }
        existingDeliveryMethod.setName(deliveryMethodRequestDto.getName());
        existingDeliveryMethod.setPrice(deliveryMethodRequestDto.getPrice());
        existingDeliveryMethod = deliveryMethodRepository.save(existingDeliveryMethod);
        return deliveryMethodMapper.toDto(existingDeliveryMethod);
    }

    @Override
    public DeliveryMethodResponseDto deleteDeliveryMethod(Long deliveryMethodId) {
        log.info("Deleting delivery method with ID: {}", deliveryMethodId);
        DeliveryMethodEntity deliveryMethodEntity = deliveryMethodRepository.findById(deliveryMethodId)
                .orElseThrow(() -> new NotFoundException("Delivery method not found"));

        Long shopId = securityUtils.getShopIdFromToken();
        if (!deliveryMethodEntity.getShop().getId().equals(shopId)) {
            throw new IllegalArgumentException("Delivery method does not belong to the specified shop");
        }
        deliveryMethodRepository.delete(deliveryMethodEntity);
        return deliveryMethodMapper.toDto(deliveryMethodEntity);
    }

    @Override
    public List<DeliveryMethodResponseDto> getAllDeliveryMethods() {
        log.info("Fetching all delivery methods");
        Long shopId = securityUtils.getShopIdFromToken();
        return deliveryMethodRepository.findByShopId(shopId).stream()
                .map(deliveryMethodMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public DeliveryMethodResponseDto getDeliveryMethod(Long deliveryMethodId) {
        log.info("Fetching delivery method with ID: {}", deliveryMethodId);
        Long shopId = securityUtils.getShopIdFromToken();
        DeliveryMethodEntity deliveryMethodEntity = deliveryMethodRepository.findById(deliveryMethodId)
                .orElseThrow(() -> new NotFoundException("Delivery method not found"));
        if (!deliveryMethodEntity.getShop().getId().equals(shopId)) {
            throw new IllegalArgumentException("Delivery method does not belong to the specified shop");
        }
        return deliveryMethodMapper.toDto(deliveryMethodEntity);
    }
}
