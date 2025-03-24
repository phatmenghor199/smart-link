package com.menghor.smart_shop.feature.setting.dto.request;

import lombok.Data;

@Data
public class UserSettingRequestDto {
    private Long userId;
    private boolean page1Enabled;
    private boolean page2Enabled;
    private boolean page3Enabled;
    private boolean page4Enabled;
    private boolean page5Enabled;
    private boolean page6Enabled;
    private boolean page7Enabled;
    private boolean page8Enabled;
    private boolean page9Enabled;
    private boolean page10Enabled;
    private boolean page11Enabled; // New page added
}