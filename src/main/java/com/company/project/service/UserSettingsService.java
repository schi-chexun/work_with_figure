package com.company.project.service;

import com.company.project.dto.SettingsUpdateRequest;
import com.company.project.entity.UserSettings;

/**
 * 用户设置服务接口
 */
public interface UserSettingsService {

    /**
     * 获取用户设置
     */
    UserSettings getByUserId(Long userId);

    /**
     * 创建默认设置
     */
    UserSettings createDefault(Long userId);

    /**
     * 更新用户设置
     */
    void update(Long userId, SettingsUpdateRequest request);

}
