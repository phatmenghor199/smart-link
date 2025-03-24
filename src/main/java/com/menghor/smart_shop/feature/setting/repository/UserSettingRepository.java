package com.menghor.smart_shop.feature.setting.repository;

import com.menghor.smart_shop.feature.setting.model.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSettingRepository extends JpaRepository<UserSetting, Long> {
    UserSetting findByUser_Id(Long userId);
}