package com.menghor.smart_shop.feature.setting.mapper;

import com.menghor.smart_shop.feature.setting.dto.request.UserSettingRequestDto;
import com.menghor.smart_shop.feature.setting.dto.resposne.UserSettingResponseDto;
import com.menghor.smart_shop.feature.setting.model.UserSetting;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserSettingMapper {
    UserSettingMapper INSTANCE = Mappers.getMapper(UserSettingMapper.class);

    @Mapping(source = "user.id", target = "userId")
    UserSettingResponseDto toResponseDto(UserSetting userSetting);

    @Mapping(source = "userId", target = "user.id")
    UserSetting toEntity(UserSettingRequestDto userSettingRequestDto);

    @Mapping(source = "userId", target = "user.id")
    void updateEntityFromDto(UserSettingRequestDto userSettingRequestDto, @MappingTarget UserSetting userSetting);
}