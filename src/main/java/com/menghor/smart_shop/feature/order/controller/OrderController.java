package com.menghor.smart_shop.feature.order.controller;

import java.util.List;

import com.menghor.smart_shop.feature.order.dto.request.OrderCheckoutRequestDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.menghor.smart_shop.enumations.OrderStatus;
import com.menghor.smart_shop.exceptions.response.ApiResponse;
import com.menghor.smart_shop.feature.order.dto.request.OrderRequestDto;
import com.menghor.smart_shop.feature.order.dto.response.OrderResponseDto;
import com.menghor.smart_shop.feature.order.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;


    /**
     * Create a new order
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OrderResponseDto> createOrder(@RequestBody OrderRequestDto orderRequest) {
        log.info("Creating order: {}", orderRequest);
        return new ApiResponse<>("Success", "Order created successfully", orderService.createOrder(orderRequest));
    }

    /**
     *  Get order by id
     */
    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponseDto> getOrder(@PathVariable Long orderId) {
        log.info("Getting order by id: {}", orderId);
        return new ApiResponse<>("Success", "Order by id response successfully", orderService.getOrder(orderId));
    }

    /**
     * Get order by phone number
     */
    @GetMapping("/customer/{phoneNumber}")
    public ApiResponse<List<OrderResponseDto>> getOrdersByPhoneNumber(@PathVariable String phoneNumber) {
        log.info("Getting orders by phone number: {}", phoneNumber);
        return new ApiResponse<>("Success", "Order by phone number response successfully", orderService.getOrdersByPhoneNumber(phoneNumber));
    }

    /**
     * Get order by shop
     */
    @GetMapping("/shop")
    public ApiResponse<List<OrderResponseDto>> getOrdersByShop() {
        log.info("Getting orders by shop");
        return new ApiResponse<>("Success", "Order by shop response successfully", orderService.getOrdersByShop());
    }

    /**
     * Update order status
     */
    @PutMapping("/{orderId}/status")
    public ApiResponse<OrderResponseDto> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {

        log.info("Updating order status for order id: {} to status: {}", orderId, status);

        OrderResponseDto updatedOrder = orderService.updateOrderStatus(orderId, status);
        return new ApiResponse<>("Success", "Updated order status  successfully", updatedOrder);
    }

    /**
     * Checkout cart and create order
     */
    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OrderResponseDto> checkout(@RequestBody OrderCheckoutRequestDto orderRequestDto) {
        log.info("Checking out cart and creating order");
        return new ApiResponse<>("Success", "Order created successfully", orderService.checkout(orderRequestDto));
    }
}
