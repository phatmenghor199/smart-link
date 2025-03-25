package com.menghor.smart_shop.feature.auth.mapper;

import com.menghor.smart_shop.enumations.RoleEnum;
import com.menghor.smart_shop.feature.auth.dto.resposne.UserDto;
import com.menghor.smart_shop.feature.auth.dto.resposne.UserResponseDto;
import com.menghor.smart_shop.feature.auth.models.Role;
import com.menghor.smart_shop.feature.auth.models.UserEntity;
import com.menghor.smart_shop.feature.customer.dto.resposne.ShopResponseDto;
import com.menghor.smart_shop.feature.customer.models.ShopEntity;
import com.menghor.smart_shop.feature.setting.dto.resposne.SubscriptionResponseDto;
import com.menghor.smart_shop.feature.setting.mapper.SubscriptionMapper;
import com.menghor.smart_shop.feature.setting.model.SubscriptionEntity;
import com.menghor.smart_shop.feature.setting.repository.SubscriptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
@Slf4j
public abstract class UserMapper {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    @Lazy
    private SubscriptionMapper subscriptionMapper;

    @Mapping(source = "roles", target = "userRole")
    @Mapping(source = "shop", target = "shop")
    @Mapping(target = "activeSubscription", ignore = true)
    @Mapping(target = "hasActiveSubscription", ignore = true)
    public abstract UserDto toDto(UserEntity user);

    public abstract UserResponseDto toPageDto(List<UserDto> content, Page<UserEntity> userPage);

    /**
     * Bulk method to fetch and set active subscriptions for multiple users
     */
    public List<UserDto> enrichUsersWithSubscriptions(List<UserEntity> users) {
        // Get all user IDs
        List<Long> userIds = users.stream()
                .map(UserEntity::getId)
                .collect(Collectors.toList());

        // Fetch active subscriptions for these users in a single query
        LocalDateTime now = LocalDateTime.now();
        List<SubscriptionEntity> activeSubscriptions = subscriptionRepository
                .findActiveSubscriptionsForUsers(userIds, now);

        // Create a map of user ID to their most recent active subscription
        Map<Long, SubscriptionEntity> subscriptionMap = activeSubscriptions.stream()
                .collect(Collectors.toMap(
                        sub -> sub.getUser().getId(),
                        sub -> sub,
                        (sub1, sub2) -> sub1.getCreatedAt().isAfter(sub2.getCreatedAt()) ? sub1 : sub2
                ));

        // Map users with subscription info
        return users.stream()
                .map(user -> {
                    UserDto userDto = toDto(user);

                    // Use the active subscription from our map if it exists
                    SubscriptionEntity subscription = subscriptionMap.get(user.getId());
                    if (subscription != null) {
                        SubscriptionResponseDto subscriptionDto = subscriptionMapper.toDto(subscription);
                        userDto.setActiveSubscription(subscriptionDto);
                        userDto.setHasActiveSubscription(true);
                    } else {
                        userDto.setActiveSubscription(null);
                        userDto.setHasActiveSubscription(false);
                    }

                    return userDto;
                })
                .collect(Collectors.toList());
    }

    public RoleEnum mapRoles(List<Role> roles) {
        // Handle case if there are roles; for example, pick the first role if available
        if (roles != null && !roles.isEmpty()) {
            return RoleEnum.valueOf(roles.get(0).getName().name()); // Convert the first role to RoleEnum
        }
        return null;  // Return null if no roles are present
    }

    public ShopResponseDto mapShopToShopDto(ShopEntity shop) {
        if (shop == null) return null;  // If shop is null, return null
        ShopResponseDto shopDto = new ShopResponseDto();
        shopDto.setId(shop.getId());
        shopDto.setName(shop.getName());
        shopDto.setLocation(shop.getLocation());
        shopDto.setCreatedAt(shop.getCreatedAt());
        shopDto.setUpdatedAt(shop.getUpdatedAt());
        return shopDto;
    }
}