package com.menghor.smart_shop.feature.order.service.impl;

import com.menghor.smart_shop.enumations.OrderStatus;
import com.menghor.smart_shop.exceptoins.error.NotFoundException;
import com.menghor.smart_shop.feature.order.dto.request.OrderItemRequestDto;
import com.menghor.smart_shop.feature.order.dto.request.OrderRequestDto;
import com.menghor.smart_shop.feature.order.dto.response.OrderResponseDto;
import com.menghor.smart_shop.feature.order.mapper.OrderMapper;
import com.menghor.smart_shop.feature.order.model.OrderEntity;
import com.menghor.smart_shop.feature.order.model.OrderItemEntity;
import com.menghor.smart_shop.feature.order.repository.OrderItemRepository;
import com.menghor.smart_shop.feature.order.repository.OrderRepository;
import com.menghor.smart_shop.feature.order.service.OrderService;
import com.menghor.smart_shop.feature.customer.models.ProductEntity;
import com.menghor.smart_shop.feature.customer.models.ShopEntity;
import com.menghor.smart_shop.feature.customer.repository.ProductRepository;
import com.menghor.smart_shop.feature.customer.repository.ShopRepository;
import com.menghor.smart_shop.utils.database.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final SecurityUtils securityUtils;

    @Transactional
    @Override
    public OrderResponseDto createOrder(OrderRequestDto orderRequest) {

        Long shopId = orderRequest.getShopId();

        // Ensure the shop exists
        ShopEntity shop =  shopRepository.findById(shopId).orElseThrow(() -> new NotFoundException("Shop not found"));

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setPhoneNumber(orderRequest.getPhoneNumber());
        orderEntity.setLocation(orderRequest.getLocation());
        orderEntity.setStatus(OrderStatus.PENDING); // Default status
        orderEntity.setShop(shop); // Set shop reference

        List<OrderItemEntity> orderItems = new ArrayList<>();
        double totalAmount = 0;
        for (OrderItemRequestDto itemRequest : orderRequest.getOrderItems()) {
            ProductEntity product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found"));

            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(product.getPrice());

            totalAmount += orderItem.getPrice() * orderItem.getQuantity();
            orderItem.setOrder(orderEntity); // Associate item with order

            orderItems.add(orderItem);
        }

        orderEntity.setTotalAmount(totalAmount);
        orderEntity.setOrderItems(orderItems); // Set order items in the order

        // Save the order first (this ensures the order is persisted before its items)
        orderRepository.save(orderEntity);

        return orderMapper.toDto(orderEntity);
    }

    @Override
    public OrderResponseDto getOrder(Long orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        return orderMapper.toDto(orderEntity);
    }

    @Override
    public List<OrderResponseDto> getOrdersByPhoneNumber(String number) {
        List<OrderEntity> orders = orderRepository.findByPhoneNumber(number);
        return orders.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponseDto> getOrdersByShop() {
        Long shopId = securityUtils.getShopIdFromToken();
        shopRepository.findById(shopId).orElseThrow(() -> new NotFoundException("Shop not found"));

        List<OrderEntity> orders = orderRepository.findByShopId(shopId);

        // Return the orders as a list of OrderResponseDto
        return orders.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDto updateOrderStatus(Long orderId, OrderStatus status) {
        // Find the order by ID
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        orderMapper.updateOrderFromDto(status, order);
        OrderEntity updatedOrder = orderRepository.save(order);

        return orderMapper.toDto(updatedOrder);
    }
}
