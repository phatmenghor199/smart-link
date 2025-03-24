package com.menghor.smart_shop.feature.order.service;

import com.menghor.smart_shop.feature.order.dto.request.DeliveryMethodRequestDto;
import com.menghor.smart_shop.feature.order.dto.response.DeliveryMethodResponseDto;

import java.util.List;

public interface DeliveryMethodService {
    DeliveryMethodResponseDto createDeliveryMethod(DeliveryMethodRequestDto deliveryMethodRequestDto);
    DeliveryMethodResponseDto updateDeliveryMethod(Long deliveryMethodId, DeliveryMethodRequestDto deliveryMethodRequestDto);
    DeliveryMethodResponseDto deleteDeliveryMethod(Long deliveryMethodId);
    List<DeliveryMethodResponseDto> getAllDeliveryMethods();
    DeliveryMethodResponseDto getDeliveryMethod(Long deliveryMethodId);
}