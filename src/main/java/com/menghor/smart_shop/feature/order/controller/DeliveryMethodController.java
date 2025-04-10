package com.menghor.smart_shop.feature.order.controller;

import com.menghor.smart_shop.exceptions.response.ApiResponse;
import com.menghor.smart_shop.feature.order.dto.request.DeliveryMethodRequestDto;
import com.menghor.smart_shop.feature.order.dto.response.DeliveryMethodResponseDto;
import com.menghor.smart_shop.feature.order.service.DeliveryMethodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/delivery-method")
@RequiredArgsConstructor
@Slf4j
public class DeliveryMethodController {

    private final DeliveryMethodService deliveryMethodService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<DeliveryMethodResponseDto> createDeliveryMethod(@RequestBody DeliveryMethodRequestDto deliveryMethodRequestDto) {
        DeliveryMethodResponseDto createdDeliveryMethod = deliveryMethodService.createDeliveryMethod(deliveryMethodRequestDto);
        return new ApiResponse<>("Success", "Delivery created successfully", createdDeliveryMethod);
    }

    @PutMapping("/{deliveryMethodId}")
    public ApiResponse<DeliveryMethodResponseDto> updateDeliveryMethod(@PathVariable Long deliveryMethodId, @RequestBody DeliveryMethodRequestDto deliveryMethodRequestDto) {
        DeliveryMethodResponseDto updatedDeliveryMethod = deliveryMethodService.updateDeliveryMethod(deliveryMethodId, deliveryMethodRequestDto);

        return new ApiResponse<>("Success", "Delivery updated successfully", updatedDeliveryMethod);
    }

    @DeleteMapping("/{deliveryMethodId}")
    public ApiResponse<DeliveryMethodResponseDto> deleteDeliveryMethod(@PathVariable Long deliveryMethodId) {
        final DeliveryMethodResponseDto deliveryMethodResponseDto = deliveryMethodService.deleteDeliveryMethod(deliveryMethodId);
        return new ApiResponse<>("Success", "Delivery deleted successfully", deliveryMethodResponseDto);
    }

    @GetMapping("/shop")
    public ApiResponse<List<DeliveryMethodResponseDto>> getAllDeliveryMethods() {
        List<DeliveryMethodResponseDto> deliveryMethods = deliveryMethodService.getAllDeliveryMethods();
        return new ApiResponse<>("Success", "Get all Delivery successfully", deliveryMethods);
    }

    @GetMapping("/{deliveryMethodId}")
    public ApiResponse<DeliveryMethodResponseDto> getDeliveryMethod(@PathVariable Long deliveryMethodId) {
        DeliveryMethodResponseDto deliveryMethodResponseDto = deliveryMethodService.getDeliveryMethod(deliveryMethodId);
        return new ApiResponse<>("Success", "Get Delivery by id successfully", deliveryMethodResponseDto);
    }
}
