package com.menghor.smart_shop.feature.setting.mapper;

import com.menghor.smart_shop.feature.setting.dto.resposne.SubscriptionHistoryResponseDto;
import com.menghor.smart_shop.feature.setting.model.SubscriptionHistoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {PlanMapper.class})
public interface SubscriptionHistoryMapper {
    SubscriptionHistoryMapper INSTANCE = Mappers.getMapper(SubscriptionHistoryMapper.class);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    SubscriptionHistoryResponseDto toDto(SubscriptionHistoryEntity entity);
}