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
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    @Lazy
    private SubscriptionMapper subscriptionMapper;

    @Mapping(source = "roles", target = "userRole")
    @Mapping(source = "shop", target = "shop")
    @Mapping(target = "activeSubscription", ignore = true)
    @Mapping(target = "hasActiveSubscription", constant = "false")
    public abstract UserDto toDto(UserEntity user);

    public abstract UserResponseDto toPageDto(List<UserDto> content, Page<UserEntity> userPage);

    @AfterMapping
    protected void setDefaultValues(@MappingTarget UserDto userDto) {
        // Ensure hasActiveSubscription is never null
        if (userDto.getHasActiveSubscription() == null) {
            userDto.setHasActiveSubscription(false);
        }
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

    // Map active subscription
    public SubscriptionResponseDto mapActiveSubscription(UserEntity user) {
        if (user == null || user.getId() == null) {
            return null;
        }

        Optional<SubscriptionEntity> subscription =
                subscriptionRepository.findActiveSubscriptionForUser(user.getId(), LocalDateTime.now());

        return subscription.map(subscriptionMapper::toDto).orElse(null);
    }

    // Check if user has active subscription
    public Boolean checkHasActiveSubscription(UserEntity user) {
        if (user == null || user.getId() == null) {
            return false;
        }

        return subscriptionRepository.hasActiveSubscription(user.getId(), LocalDateTime.now());
    }

}
