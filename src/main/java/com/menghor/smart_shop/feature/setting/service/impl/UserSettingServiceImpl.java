package com.menghor.smart_shop.feature.setting.service.impl;

import com.menghor.smart_shop.feature.setting.dto.request.UserSettingRequestDto;
import com.menghor.smart_shop.feature.setting.dto.resposne.UserSettingResponseDto;
import com.menghor.smart_shop.feature.setting.mapper.UserSettingMapper;
import com.menghor.smart_shop.feature.setting.model.UserSetting;
import com.menghor.smart_shop.feature.setting.repository.UserSettingRepository;
import com.menghor.smart_shop.feature.setting.service.UserSettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSettingServiceImpl implements UserSettingService {
    private final UserSettingRepository userSettingRepository;
    private final UserSettingMapper userSettingMapper;
    @Override
    public UserSettingResponseDto getUserSetting(Long userId) {
        UserSetting userSetting = userSettingRepository.findByUser_Id(userId);
        return userSettingMapper.toResponseDto(userSetting);
    }

    @Override
    public UserSettingResponseDto createUserSetting(UserSettingRequestDto userSettingRequestDto) {
        UserSetting userSetting = userSettingMapper.toEntity(userSettingRequestDto);
        UserSetting savedSetting = userSettingRepository.save(userSetting);
        return userSettingMapper.toResponseDto(savedSetting);
    }

    @Override
    public UserSettingResponseDto updateUserSetting(Long userId, UserSettingRequestDto userSettingRequestDto) {
        UserSetting existingSetting = userSettingRepository.findByUser_Id(userId);
        userSettingMapper.updateEntityFromDto(userSettingRequestDto, existingSetting);
        UserSetting updatedSetting = userSettingRepository.save(existingSetting);
        return userSettingMapper.toResponseDto(updatedSetting);
    }

    @Override
    public void deleteUserSetting(Long userId) {
        UserSetting userSetting = userSettingRepository.findByUser_Id(userId);
        userSettingRepository.delete(userSetting);
    }

    @Override
    public List<UserSettingResponseDto> getAllUserSettings() {
        return userSettingRepository.findAll().stream()
                .map(userSettingMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
