package com.menghor.smart_shop.feature.setting.model;

import com.menghor.smart_shop.feature.auth.models.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "user_settings")
public class UserSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

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
    private boolean page11Enabled;
}