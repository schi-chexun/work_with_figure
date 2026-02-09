package com.company.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.company.project.dto.SettingsUpdateRequest;
import com.company.project.entity.UserSettings;
import com.company.project.mapper.UserSettingsMapper;
import com.company.project.service.UserSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 用户设置服务实现
 */
@Service
@RequiredArgsConstructor
public class UserSettingsServiceImpl implements UserSettingsService {

    private final UserSettingsMapper userSettingsMapper;

    @Override
    public UserSettings getByUserId(Long userId) {
        LambdaQueryWrapper<UserSettings> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserSettings::getUserId, userId);
        UserSettings settings = userSettingsMapper.selectOne(wrapper);

        // 如果没有设置，创建默认设置
        if (settings == null) {
            settings = createDefault(userId);
        }

        return settings;
    }

    @Override
    public UserSettings createDefault(Long userId) {
        UserSettings settings = new UserSettings();
        settings.setUserId(userId);
        settings.setThemeId(1L); // 默认主题：指尖农场
        settings.setWorkDuration(25);
        settings.setShortBreak(5);
        settings.setLongBreak(15);
        settings.setLongBreakInterval(4);
        settings.setSoundEnabled(1);
        settings.setWaterReminder(1);
        settings.setWaterInterval(30);
        settings.setOverworkReminder(1);
        settings.setOverworkThreshold(120);

        userSettingsMapper.insert(settings);
        return settings;
    }

    @Override
    public void update(Long userId, SettingsUpdateRequest request) {
        LambdaUpdateWrapper<UserSettings> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserSettings::getUserId, userId);

        // 只更新非空字段
        if (request.getThemeId() != null) {
            wrapper.set(UserSettings::getThemeId, request.getThemeId());
        }
        if (request.getWorkDuration() != null) {
            wrapper.set(UserSettings::getWorkDuration, request.getWorkDuration());
        }
        if (request.getShortBreak() != null) {
            wrapper.set(UserSettings::getShortBreak, request.getShortBreak());
        }
        if (request.getLongBreak() != null) {
            wrapper.set(UserSettings::getLongBreak, request.getLongBreak());
        }
        if (request.getLongBreakInterval() != null) {
            wrapper.set(UserSettings::getLongBreakInterval, request.getLongBreakInterval());
        }
        if (request.getSoundEnabled() != null) {
            wrapper.set(UserSettings::getSoundEnabled, request.getSoundEnabled());
        }
        if (request.getWaterReminder() != null) {
            wrapper.set(UserSettings::getWaterReminder, request.getWaterReminder());
        }
        if (request.getWaterInterval() != null) {
            wrapper.set(UserSettings::getWaterInterval, request.getWaterInterval());
        }
        if (request.getOverworkReminder() != null) {
            wrapper.set(UserSettings::getOverworkReminder, request.getOverworkReminder());
        }
        if (request.getOverworkThreshold() != null) {
            wrapper.set(UserSettings::getOverworkThreshold, request.getOverworkThreshold());
        }

        userSettingsMapper.update(null, wrapper);
    }

}
