package com.menghor.smart_shop.feature.order.service.impl;

import com.menghor.smart_shop.enumations.DeliveryCharge;
import com.menghor.smart_shop.enumations.OrderFilterType;
import com.menghor.smart_shop.enumations.OrderStatus;
import com.menghor.smart_shop.exceptoins.error.BadRequestException;
import com.menghor.smart_shop.exceptoins.error.NotFoundException;
import com.menghor.smart_shop.feature.customer.models.ProductSizeEntity;
import com.menghor.smart_shop.feature.customer.repository.ProductSizeRepository;
import com.menghor.smart_shop.feature.order.dto.request.OrderCheckoutRequestDto;
import com.menghor.smart_shop.feature.order.dto.request.OrderItemRequestDto;
import com.menghor.smart_shop.feature.order.dto.request.OrderRequestDto;
import com.menghor.smart_shop.feature.order.dto.response.OrderResponseDto;
import com.menghor.smart_shop.feature.order.mapper.OrderMapper;
import com.menghor.smart_shop.feature.order.model.*;
import com.menghor.smart_shop.feature.order.repository.CartRepository;
import com.menghor.smart_shop.feature.order.repository.DeliveryMethodRepository;
import com.menghor.smart_shop.feature.order.repository.OrderItemRepository;
import com.menghor.smart_shop.feature.order.repository.OrderRepository;
import com.menghor.smart_shop.feature.order.service.CartService;
import com.menghor.smart_shop.feature.order.service.OrderService;
import com.menghor.smart_shop.feature.customer.models.ProductEntity;
import com.menghor.smart_shop.feature.customer.models.ShopEntity;
import com.menghor.smart_shop.feature.customer.repository.ProductRepository;
import com.menghor.smart_shop.feature.customer.repository.ShopRepository;
import com.menghor.smart_shop.utils.database.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    private final ProductSizeRepository productSizeRepository;
    private final CartRepository cartRepository;
    private final DeliveryMethodRepository deliveryMethodRepository;
    private final CartService cartService;

    @Transactional
    @Override
    public OrderResponseDto createOrder(OrderRequestDto orderRequest) {
        log.info("Creating order");

        Long shopId = orderRequest.getShopId();
        ShopEntity shop = shopRepository.findById(shopId).orElseThrow(() -> new NotFoundException("Shop not found"));

        DeliveryMethodEntity deliveryMethod = deliveryMethodRepository.findById(orderRequest.getDeliveryMethodId())
                .orElseThrow(() -> new NotFoundException("Delivery method not found"));

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setPhoneNumber(orderRequest.getPhoneNumber());
        orderEntity.setLocation(orderRequest.getLocation());
        orderEntity.setStatus(OrderStatus.PENDING); // Default status
        orderEntity.setOrderBy(OrderFilterType.BY_CUSTOMER);
        orderEntity.setDeliveryMethod(deliveryMethod); // Set delivery method
        orderEntity.setShop(shop); // Set shop reference

        List<OrderItemEntity> orderItems = new ArrayList<>();
        double deliveryFee = orderRequest.getIsFreeDelivery() ? 0 : deliveryMethod.getPrice(); // Include delivery price if not free
        orderEntity.setDeliveryCharge(orderRequest.getIsFreeDelivery() ? DeliveryCharge.FREE : DeliveryCharge.PAID);
        orderEntity.setDeliveryFee(deliveryFee);
        double totalAmount = 0;

        for (OrderItemRequestDto itemRequest : orderRequest.getOrderItems()) {
            ProductEntity product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found"));

            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setProduct(product);

            if (itemRequest.getSizeId() != null) {
                log.info("Size ID: {}", itemRequest.getSizeId());
                ProductSizeEntity size = productSizeRepository.findById(itemRequest.getSizeId())
                        .orElseThrow(() -> new NotFoundException("Size not found"));
                orderItem.setSize(size);
                orderItem.setPrice(size.getFinalPrice());
                orderItem.setDiscountType(size.getDiscountType());
                orderItem.setDiscountValue(size.getDiscountValue());

                productSizeRepository.save(size);
            } else {
                log.info("No size ID");
                orderItem.setPrice(product.getPrice());
                orderItem.setDiscountType(product.getDiscountType());
                orderItem.setDiscountValue(product.getDiscountValue());

                productRepository.save(product);
            }

            // Ensure price is not null
            if (orderItem.getPrice() == null) {
                throw new IllegalStateException("Price cannot be null for order item: " + itemRequest.getProductId());
            }

            orderItem.setQuantity(itemRequest.getQuantity());
            totalAmount += orderItem.getPrice() * orderItem.getQuantity();
            orderItem.setOrder(orderEntity);
            orderItems.add(orderItem);
        }

        totalAmount += deliveryFee; // Add delivery fee to the total amount
        orderEntity.setTotalAmount(totalAmount);
        orderEntity.setOrderItems(orderItems); // Set order items in the order
        orderRepository.save(orderEntity);

        log.info("Order created successfully");
        return orderMapper.toDto(orderEntity);
    }

    @Override
    public OrderResponseDto getOrder(Long orderId) {
        log.info("Getting order by ID");
        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        log.info("Order found successfully");
        return orderMapper.toDto(orderEntity);
    }

    @Override
    public List<OrderResponseDto> getOrdersByPhoneNumber(String number) {
        log.info("Getting orders by phone number");
        List<OrderEntity> orders = orderRepository.findByPhoneNumber(number);
        log.info("Found {} orders for phone number: {}", orders.size(), number);
        return orders.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponseDto> getOrdersByShop() {
        log.info("Getting orders by shop");

        Long shopId = securityUtils.getShopIdFromToken();
        shopRepository.findById(shopId).orElseThrow(() -> new NotFoundException("Shop not found"));
        List<OrderEntity> orders = orderRepository.findByShopId(shopId);

        log.info("Found {} orders for shop with ID: {}", orders.size(), shopId);
        // Return the orders as a list of OrderResponseDto
        return orders.stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDto updateOrderStatus(Long orderId, OrderStatus status) {
        log.info("Updating order status");
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        orderMapper.updateOrderFromDto(status, order);

        OrderEntity updatedOrder = orderRepository.save(order);

        // Increase stock quantity if order is cancelled
//        if (status == OrderStatus.CANCELLED) {
//            for (OrderItemEntity item : order.getOrderItems()) {
//                log.info("Updating stock quantity for product: {}", item.getProduct().getName());
//                if (item.getSize() != null) {
//                    ProductSizeEntity size = item.getSize();
//                    size.setStockQuantity(size.getStockQuantity() + item.getQuantity());
//                    productSizeRepository.save(size);
//                } else {
//                    ProductEntity product = item.getProduct();
//                    product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
//                    productRepository.save(product);
//                }
//            }
//        }

        log.info("Order status updated successfully");
        return orderMapper.toDto(updatedOrder);
    }

    @Transactional
    @Override
    public OrderResponseDto checkout(OrderCheckoutRequestDto orderRequest) {
        Long shopId = securityUtils.getShopIdFromToken();
        log.info("Checking out cart and creating order for shop with ID: {}", shopId);
        CartEntity cart = cartRepository.findByShopId(shopId).stream().findFirst()
                .orElseThrow(() -> new NotFoundException("Cart not found"));

        // Check if there are items in the cart
        if (cart.getCartItems().isEmpty()) {
            log.warn("Cannot checkout an empty cart");
            throw new BadRequestException("Cannot checkout an empty cart");
        }

        // Create a new order
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setShop(cart.getShop());
        orderEntity.setStatus(OrderStatus.COMPLETED); // Set default order status
        orderEntity.setOrderBy(OrderFilterType.BY_SHOP);
        orderEntity.setPhoneNumber(orderRequest.getPhoneNumber());
        orderEntity.setLocation(orderRequest.getLocation());

        DeliveryMethodEntity deliveryMethod = deliveryMethodRepository.findById(orderRequest.getDeliveryMethodId())
                .orElseThrow(() -> new NotFoundException("Delivery method not found"));
        orderEntity.setDeliveryMethod(deliveryMethod); // Set delivery method

        List<OrderItemEntity> orderItems = new ArrayList<>();
        double deliveryFee = orderRequest.getIsFreeDelivery() ? 0 : deliveryMethod.getPrice(); // Include delivery price if not free
        orderEntity.setDeliveryCharge(orderRequest.getIsFreeDelivery() ? DeliveryCharge.FREE : DeliveryCharge.PAID);
        orderEntity.setDeliveryFee(deliveryFee);
        double totalAmount = 0;

        for (CartItemEntity cartItem : cart.getCartItems()) {
            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setDiscountType(cartItem.getDiscountType());
            orderItem.setDiscountValue(cartItem.getDiscountValue());

            ProductSizeEntity size = cartItem.getSize();
            if (size != null) {
                orderItem.setSize(size);
//                if (size.getStockQuantity() == null || size.getStockQuantity() < orderItem.getQuantity()) {
//                    throw new BadRequestException("Product size out of stock");
//                }
                size.setStockQuantity(size.getStockQuantity());
                productSizeRepository.save(size);
            } else {
                ProductEntity product = cartItem.getProduct();
//                if (product.getStockQuantity() == null || product.getStockQuantity() < orderItem.getQuantity()) {
//                    throw new BadRequestException("Product out of stock");
//                }
                product.setStockQuantity(product.getStockQuantity());
                productRepository.save(product);
            }

            // Update total amount
            totalAmount += orderItem.getPrice() * orderItem.getQuantity();
            orderItem.setOrder(orderEntity);
            orderItems.add(orderItem);
        }

        totalAmount += deliveryFee; // Add delivery fee to the total amount
        orderEntity.setTotalAmount(totalAmount);
        orderEntity.setOrderItems(orderItems); // Set order items in the order
        log.info("Saving order");
        orderRepository.save(orderEntity);

        // Clear the cart items for the shop
        cartService.clearCart();

        log.info("Order created successfully");
        return orderMapper.toDto(orderEntity);
    }
}
