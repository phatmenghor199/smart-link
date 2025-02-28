package com.menghor.smart_shop.feature.order.service;

import com.menghor.smart_shop.enumations.OrderStatus;
import com.menghor.smart_shop.feature.order.dto.request.OrderRequestDto;
import com.menghor.smart_shop.feature.order.dto.response.OrderResponseDto;

import java.util.List;

public interface OrderService {
    OrderResponseDto createOrder(OrderRequestDto orderRequest);
    OrderResponseDto getOrder(Long orderId);
    List<OrderResponseDto> getOrdersByPhoneNumber(String number);
    List<OrderResponseDto> getOrdersByShop();

    OrderResponseDto updateOrderStatus(Long orderId, OrderStatus status);
}

