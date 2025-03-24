package com.menghor.smart_shop.feature.setting.service;

import com.menghor.smart_shop.feature.setting.dto.request.UserSettingRequestDto;
import com.menghor.smart_shop.feature.setting.dto.resposne.UserSettingResponseDto;

import java.util.List;

public interface UserSettingService {
    UserSettingResponseDto getUserSetting(Long userId);
    UserSettingResponseDto createUserSetting(UserSettingRequestDto userSettingRequestDto);
    UserSettingResponseDto updateUserSetting(Long userId, UserSettingRequestDto userSettingRequestDto);
    void deleteUserSetting(Long userId);
    List<UserSettingResponseDto> getAllUserSettings();
}