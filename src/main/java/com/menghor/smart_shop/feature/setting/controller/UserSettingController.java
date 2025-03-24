package com.menghor.smart_shop.feature.setting.controller;

import com.menghor.smart_shop.feature.setting.dto.request.UserSettingRequestDto;
import com.menghor.smart_shop.feature.setting.dto.resposne.UserSettingResponseDto;
import com.menghor.smart_shop.feature.setting.service.UserSettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
@Slf4j
public class UserSettingController {
    private final UserSettingService userSettingService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserSettingResponseDto> getUserSetting(@PathVariable Long userId) {
        log.info("Getting user setting for user with id: {}", userId);
        UserSettingResponseDto userSetting = userSettingService.getUserSetting(userId);
        return ResponseEntity.ok(userSetting);
    }

    @PostMapping
    public ResponseEntity<UserSettingResponseDto> createUserSetting(@RequestBody UserSettingRequestDto userSettingRequestDto) {
        log.info("Creating user setting for user with id: {}", userSettingRequestDto.getUserId());
        UserSettingResponseDto createdSetting = userSettingService.createUserSetting(userSettingRequestDto);
        return ResponseEntity.ok(createdSetting);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserSettingResponseDto> updateUserSetting(@PathVariable Long userId, @RequestBody UserSettingRequestDto userSettingRequestDto) {
        log.info("Updating user setting for user with id: {}", userId);
        UserSettingResponseDto updatedSetting = userSettingService.updateUserSetting(userId, userSettingRequestDto);
        return ResponseEntity.ok(updatedSetting);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUserSetting(@PathVariable Long userId) {
        log.info("Deleting user setting for user with id: {}", userId);
        userSettingService.deleteUserSetting(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserSettingResponseDto>> getAllUserSettings() {
        log.info("Getting all user settings");
        List<UserSettingResponseDto> userSettings = userSettingService.getAllUserSettings();
        return ResponseEntity.ok(userSettings);
    }
}