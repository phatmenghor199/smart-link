package com.menghor.smart_shop.feature.setting.mapper;

import com.menghor.smart_shop.feature.auth.mapper.UserMapper;
import com.menghor.smart_shop.feature.setting.dto.request.SubscriptionRequestDto;
import com.menghor.smart_shop.feature.setting.dto.resposne.SubscriptionResponseDto;
import com.menghor.smart_shop.feature.setting.model.SubscriptionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Mapper(componentModel = "spring", uses = {PlanMapper.class, UserMapper.class})
public interface SubscriptionMapper {
    SubscriptionMapper INSTANCE = Mappers.getMapper(SubscriptionMapper.class);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "plan", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    SubscriptionEntity toEntity(SubscriptionRequestDto dto);
    
    @Mapping(target = "isActive", expression = "java(calculateIsActive(subscription))")
    @Mapping(target = "daysRemaining", expression = "java(calculateDaysRemaining(subscription))")
    SubscriptionResponseDto toDto(SubscriptionEntity subscription);
    
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