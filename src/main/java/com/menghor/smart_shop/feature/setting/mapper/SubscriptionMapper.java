package com.menghor.smart_shop.feature.setting.mapper;

import com.menghor.smart_shop.enumations.RoleEnum;
import com.menghor.smart_shop.feature.auth.dto.resposne.UserDto;
import com.menghor.smart_shop.feature.auth.models.UserEntity;
import com.menghor.smart_shop.feature.setting.dto.request.SubscriptionRequestDto;
import com.menghor.smart_shop.feature.setting.dto.resposne.SubscriptionResponseDto;
import com.menghor.smart_shop.feature.setting.model.SubscriptionEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Mapper(componentModel = "spring", uses = {PlanMapper.class})
public interface SubscriptionMapper {
    SubscriptionMapper INSTANCE = Mappers.getMapper(SubscriptionMapper.class);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "plan", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    SubscriptionEntity toEntity(SubscriptionRequestDto dto);

    @Mapping(target = "user", qualifiedByName = "mapUserSummary")
    @Mapping(target = "isActive", expression = "java(calculateIsActive(subscription))")
    @Mapping(target = "daysRemaining", expression = "java(calculateDaysRemaining(subscription))")
    SubscriptionResponseDto toDto(SubscriptionEntity subscription);

    @Named("mapUserSummary")
    default UserDto mapUserSummary(UserEntity user) {
        if (user == null) return null;

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .userRole(user.getRoles() != null && !user.getRoles().isEmpty()
                        ? RoleEnum.valueOf(user.getRoles().get(0).getName().name())
                        : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    default Boolean calculateIsActive(SubscriptionEntity subscription) {
        return subscription.getStatus().name().equals("ACTIVE") &&
                subscription.getEndDate().isAfter(LocalDateTime.now());
    }

    default Long calculateDaysRemaining(SubscriptionEntity subscription) {
        if (subscription.getEndDate().isBefore(LocalDateTime.now())) {
            return 0L;
        }
        return ChronoUnit.DAYS.between(LocalDateTime.now(), subscription.getEndDate());
    }
}